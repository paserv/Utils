const express = require('express');
const app = express();
const http = require('http').createServer(app);
const io = require('socket.io')(http);

app.use("/", express.static(__dirname + '/public'));

io.on('connection', function (socket) {
    console.log(`[${dateTime()}] [${toRemoteString(socket)}] Connected to server`);

    socket.on("disconnect", function() {
        Object.keys(io.sockets.adapter.rooms).forEach(function(room){
            io.to(room).emit('userdisconnect', socket.id);
        });
    });

    socket.on('check-room', function (room) {
        console.log(`[${dateTime()}] [${toRemoteString(socket)}]` + 'Checking Room: ' + room);
        if (room in io.sockets.adapter.rooms) {
            socket.emit('check-room', false);
        } else {
            socket.emit('check-room', true);
        }
    });

    socket.on('check-nick', function (room, nick) {
        console.log(`[${dateTime()}] [${toRemoteString(socket)}]` + 'Checking Nick: ' + nick + ' for Room: ' + room);
        if (room in io.sockets.adapter.rooms) {
            if (io.sockets.adapter.rooms[room].length >= parseInt(io.sockets.adapter.rooms[room].sessionData.numMembers) ) {
                socket.emit('check-full', true);
            } else {
                var clients = io.sockets.adapter.rooms[room].sockets;
                for (var clientId in clients) {
                    var clientSocket = io.sockets.connected[clientId];
                    if (clientSocket.nickname == nick) {
                        socket.emit('check-nick', false);
                        return;
                    }
                }
                socket.emit('check-nick', true);
            }
        } else {
            socket.emit('check-room', false);
        }
    });

    socket.on('create', function (room, numMembers, nick) {
        console.log(`[${dateTime()}] [${toRemoteString(socket)}]` + 'Room: ' + room + ' Nick: ' + nick + ' Members: ' + numMembers);
        socket.nickname = nick;
        socket.join(room);
        io.sockets.adapter.rooms[room].sessionData = {};
        io.sockets.adapter.rooms[room].sessionData.numMembers = numMembers;
        console.log(`[${dateTime()}] [${toRemoteString(socket)}] Created Room: ` + room);
    });

    socket.on('join', function (room, nick) {
        console.log(`[${dateTime()}] [${toRemoteString(socket)}]` + 'Room: ' + room + ' Nick: ' + nick);
        socket.nickname = nick;
        socket.join(room);
        console.log(`[${dateTime()}] [${toRemoteString(socket)}] User: ` + nick + ' joins the room ' + room);
        let members = [];
        var clients = io.sockets.adapter.rooms[room].sockets;
        for (var clientId in clients) {
            var clientSocket = io.sockets.connected[clientId];
            members.push({nick: clientSocket.nickname, id: clientSocket.id});
        }
        io.to(room).emit('members', members);
    });

});

http.listen(8080, function () {
    console.log('listening on *:8080');
});

function toRemoteString(socket) {
    return socket.request.connection.remoteAddress + ':' + socket.request.connection.remotePort;
}

function dateTime() {
    return new Date().toLocaleString();
}