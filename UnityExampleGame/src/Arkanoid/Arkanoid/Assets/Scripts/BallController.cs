using UnityEngine;
using System.Collections;

public class BallController : MonoBehaviour {

    public float speed;

    private Rigidbody rb;


    void Start () {
        rb = GetComponent<Rigidbody>();
        Vector3 movement = new Vector3(1f, 0.0f, 2f);
        rb.AddForce(movement * speed);
        //rb.velocity = Vector3.forward * speed;
    }

    void FixedUpdate() {

    }

    void OnTriggerExit(Collider other) {
        
    }

    void OnCollisionEnter(Collision collision) {
        ContactPoint contact = collision.contacts[0];
        rb.velocity += contact.normal * 1.5f;
        if (collision.gameObject.CompareTag("brick")) {
            collision.gameObject.SetActive(false);
        }
        if (collision.gameObject.CompareTag("building")) {
            collision.gameObject.transform.Translate(0, -1, 0);
        }
    }

}
