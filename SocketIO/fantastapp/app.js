'use strict';

const app = require('express')();
app.set('view engine', 'pug');

const server = require('http').Server(app);
const io = require('socket.io')(server);

app.get('/', (req, res) => {
  res.render('index.pug');
});

const path = require('path');
app.get('/players', function (req, res) {
  res.sendFile(path.join(__dirname, 'res/players.csv'));
});

io.on('connection', socket => {
  console.log(`[${dateTime()}] [${toRemoteString(socket)}] Connected to server`);

  socket.on("disconnect", function () {
    Object.keys(io.sockets.adapter.rooms).forEach(function (room) {
      io.to(room).emit('userdisconnect', socket.id);
      //io.to(room).emit('stop', socket.id);
    });

  });

  socket.on('create', function (room, numMembers, nick) {
    if (room in io.sockets.adapter.rooms) {
      socket.emit('message', "Room not available");
    } else {
      console.log(`[${dateTime()}] [${toRemoteString(socket)}]` + 'Room: ' + room + ' Nick: ' + nick + ' Members: ' + numMembers);
      socket.nickname = nick;
      socket.join(room);
      io.sockets.adapter.rooms[room].sessionData = {};
      io.sockets.adapter.rooms[room].sessionData.numMembers = numMembers;
      console.log(`[${dateTime()}] [${toRemoteString(socket)}] Created Room: ` + room);
      socket.emit('wait', true);
    }
  });

  socket.on('join', function (room, nick) {
    console.log(`[${dateTime()}] [${toRemoteString(socket)}]` + 'Checking Nick: ' + nick + ' for Room: ' + room);
    let members = [];

    //Check if room exists
    if (room in io.sockets.adapter.rooms) {

      //Check if Nick exists
      var clients = io.sockets.adapter.rooms[room].sockets;
      for (var clientId in clients) {
        var clientSocket = io.sockets.connected[clientId];
        members.push({ nick: clientSocket.nickname, id: clientSocket.id });
        if (clientSocket.nickname == nick) {
          socket.emit('message', "Nick exists!");
          return;
        }
      }

      //If asta already in progress
      if (io.sockets.adapter.rooms[room].sessionData.members) {
        //Check if Nick is valid
        if (io.sockets.adapter.rooms[room].sessionData.members.filter(value => { return value.nick == nick; }).length == 0) {
          socket.emit('message', "Invalid Nick!");
          return;
        }
        //If valid reconnect user
        socket.nickname = nick;
        socket.join(room);
        members.push({ nick: socket.nickname, id: socket.id });
        io.to(room).emit('members', members);
        io.to(room).emit('reconnect', io.sockets.adapter.rooms[room].sessionData.currentPlayer, io.sockets.adapter.rooms[room].sessionData.bestOffering, io.sockets.adapter.rooms[room].sessionData.currentOffer);
        //If asta not in progress and Room not full
      } else if (io.sockets.adapter.rooms[room].length < parseInt(io.sockets.adapter.rooms[room].sessionData.numMembers)) {
        socket.nickname = nick;
        members.push({ nick: socket.nickname, id: socket.id });
        socket.join(room);
        io.to(room).emit('members', members);
        //If room is full
        if (io.sockets.adapter.rooms[room].length == parseInt(io.sockets.adapter.rooms[room].sessionData.numMembers)) {
          io.sockets.adapter.rooms[room].sessionData.nextUser = members.length - 1;
          io.sockets.adapter.rooms[room].sessionData.bestOffering = members[io.sockets.adapter.rooms[room].sessionData.nextUser % members.length];
          io.sockets.adapter.rooms[room].sessionData.members = members;
          //Start asta
          io.to(room).emit('start', io.sockets.adapter.rooms[room].sessionData.bestOffering);
        } else {
          socket.emit('message', "Room is full");
        }
      }
    } else {
      socket.emit('message', "Room does not exist!");
    }
  });

  socket.on('offer', function (room, player, playerteam) {
    io.sockets.adapter.rooms[room].sessionData.ackchoosed = 0;
    io.sockets.adapter.rooms[room].sessionData.membersWhoLeft = [];
    io.to(room).emit('choosed', player, playerteam);
  });

  socket.on('ackchoosed', function (room, player, playerteam) {
    io.sockets.adapter.rooms[room].sessionData.ackchoosed++;
    if (io.sockets.adapter.rooms[room].sessionData.numMembers == io.sockets.adapter.rooms[room].sessionData.ackchoosed) {
      io.sockets.adapter.rooms[room].sessionData.currentPlayer = playerteam;
      io.sockets.adapter.rooms[room].sessionData.currentOffer = 1;
      io.to(room).emit('reackchoosed', playerteam, io.sockets.adapter.rooms[room].sessionData.bestOffering, io.sockets.adapter.rooms[room].sessionData.currentOffer);
    }
  });

  socket.on('dooffer', function (room, player, playerteam, offer, nick) {
    if (parseInt(offer) > io.sockets.adapter.rooms[room].sessionData.currentOffer) {
      io.sockets.adapter.rooms[room].sessionData.currentOffer = offer;
      io.sockets.adapter.rooms[room].sessionData.currentPlayer = playerteam;
      io.sockets.adapter.rooms[room].sessionData.bestOffering = io.sockets.adapter.rooms[room].sessionData.members.filter(value => { return value.nick == nick })[0];
      io.sockets.adapter.rooms[room].sessionData.acknewoffer = 0;
      io.to(room).emit('newoffer', player, playerteam, offer, nick);
    } else {
      socket.emit('message', "Offerta troppo bassa!");
    }
  });

  socket.on('acknewoffer', function (room, player, playerteam, offer, nick) {
    io.sockets.adapter.rooms[room].sessionData.acknewoffer++;
    if (io.sockets.adapter.rooms[room].sessionData.numMembers == io.sockets.adapter.rooms[room].sessionData.acknewoffer) {
      io.sockets.adapter.rooms[room].sessionData.currentPlayer = playerteam;
      io.sockets.adapter.rooms[room].sessionData.currentOffer = offer;
      io.sockets.adapter.rooms[room].sessionData.bestOffering = io.sockets.adapter.rooms[room].sessionData.members.filter(value => { return value.nick == nick })[0];
      io.to(room).emit('reacknewoffer', playerteam, io.sockets.adapter.rooms[room].sessionData.members.filter(value => { return value.nick == nick })[0], offer);
    }
  });

  socket.on('memberleave', function (room, player, playerteam, nick) {
    let mem = io.sockets.adapter.rooms[room].sessionData.members.filter(value => { return value.nick == nick })[0];
    io.sockets.adapter.rooms[room].sessionData.membersWhoLeft.push(mem);

    io.to(room).emit('membersWhoLeft', io.sockets.adapter.rooms[room].sessionData.membersWhoLeft);

    if (io.sockets.adapter.rooms[room].sessionData.membersWhoLeft.length + 1 == io.sockets.adapter.rooms[room].sessionData.members.length) {
      console.log(nick + " gets " + playerteam + " at " + io.sockets.adapter.rooms[room].sessionData.currentOffer + " FTR - Store somewhere");
      console.log("Remove player from list");
      console.log("Add player to buyed");
      io.sockets.adapter.rooms[room].sessionData.currentPlayer = "";
      io.sockets.adapter.rooms[room].sessionData.currentOffer = 0;
      io.sockets.adapter.rooms[room].sessionData.nextUser++;
      io.sockets.adapter.rooms[room].sessionData.bestOffering = io.sockets.adapter.rooms[room].sessionData.members[io.sockets.adapter.rooms[room].sessionData.nextUser % (io.sockets.adapter.rooms[room].sessionData.members.length - 1)];
      io.to(room).emit('start', io.sockets.adapter.rooms[room].sessionData.bestOffering);
      io.to(room).emit('members', io.sockets.adapter.rooms[room].sessionData.members);

    }

  });


});

function toRemoteString(socket) {
  return socket.request.connection.remoteAddress + ':' + socket.request.connection.remotePort;
}

function dateTime() {
  return new Date().toLocaleString();
}

if (module === require.main) {
  const PORT = process.env.PORT || 8080;
  server.listen(PORT, () => {
    console.log(`App listening on port ${PORT}`);
    console.log('Press Ctrl+C to quit.');
  });
}
// [END appengine_websockets_app]