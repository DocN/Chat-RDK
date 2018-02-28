const functions = require('firebase-functions');

var admin = require("firebase-admin");

//var serviceAccount = require("firekey.json");

admin.initializeApp({
  credential: admin.credential.cert({
    "type": "service_account",
    "project_id": "chatrdk-458bf",
    "private_key_id": "bac8dbb3a527e3888978798709c3e75b87e666cb",
    "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCz1/F8+7epkS/V\n/Y06K38UopUgtG34cmtuf8gnXIhKbHSUvFLfJYm4MFgqozHFCHJii9nTIjcxN3HU\nadlMqHy3IFB4rhiGrBpM4ddys4UWyr9Mk4FJpHYKm8dvvVGwi8AgC4ght1H2C4om\n6MDsa5J77NvoXKiCV1Ub5NqUa2cnd4SdPXo1nR8EvKlq1N12QWnPQnMvc64m2G4U\nnmSz9u8OvWuawWH0QQ03uoBT8SfSfnC4heOqKg1LtdVrcM5yIdaTmGb9UqNexneG\ni56Cq2kr0UOfaBmwjHG56CGOaxMKSnPkjQLMYOZI+kkVhEsuSKKUnEyqG4chr/Re\nsBLDACepAgMBAAECggEAEb5igxRKwJ4kfBZyEqUr+ps332wt1W9kjhhpZQjKIZ2l\nuu8BtiZ7+gI7EKndRTVaI8LskNo1W9O+lViEKf4Nn/u+NMnofZEJX0CBAz4XCXKj\ngSikfuGNC+rmuG2TW7nOENtSaREvMvlcfg5Eyo6qezOZVsmdjo6TRRdFQZN/L/N+\nHgm4pSuVpU/uLQ6uwzpu7YNSentKAIid3k/PMr0gUCrGntZ/L5AAak2+5hPPR15Y\nVyPVWr9oheaYxB3UcFjmDwD1kDuTX8Ygq+xOzwSp2RzwnEcohEa68NUi+IdP9VAW\nEhfeg1Z/52nUKkaMfsGqC4QvYYWoa+u2GC0rOSxWAQKBgQD0N+3SHx/5zrnPRTVj\nym3Rb0rHE2qWUO6kk028k1DZHbBC9vk0rUSw2VraKuPZy5pa/sWkZBXhEvr9KXme\n5nwMblPk8o+XnEALqPgPTN3kXu9OTjbyvzcZFeFozyexuOlgQ1f4BaROLW1hsWQ1\nG0ky+Uzdvo7rYwawbanYTsIeYQKBgQC8hP2suXBxlmTmxvb65u3gwCpZm7A6675M\nrODyxpq4iHx1nxDdxlhLLHtQzsE8KfvuO8NlGbS01yGteGuQOLCK4cNWXWJg6GT9\nAbl1lNgEDpLdb0p1mB+cmPAA+cS/ICAXbGTsj0SpSzq6aSow8eSPoRThltxtcGw2\n4XRRKoo+SQKBgHYBLF4Sgg3f8PKRO3oLj0LapuRIooEdfCJztuU+vZsbeFbzJXlp\nm75kWD0A6nYLpnr/jhqf7d6CoFqBlu3L6vbKvKjs96tHab74l1w1RSabJMDOlEh5\nUrNBWlideE2szr+f6rrCZyELXEYPRB9RHu6NfbHL6R0eDVZktuP5Ml0hAoGASO0J\nVK/Hl6keD0gYcqayjqbHuzJG0gQv8WlO+5tobsodm0KVO7Vmom3qpi/VAHkdQ+NK\niumOxgYSuRBES94iLRd4gQhO7j34Ewd/vsR9a9RXa+GJZEQctm9TKI12VpkO0QDV\nac1iOjsn1MSp1ZU9XXJqqXm6e3tp4TEF1gAmykECgYBZF9xepSRgfIb8kEUNi3W2\nhNzKRr7z6KlYzY9Uo6OVwzfzIwYG46MZ9yJ0lCo5/N7GbVVX+zDoKdxeYC99gwuS\n+ihtXkyxGWGCVbkgnkWydZiyFXqr7s6KP8BcDTkxeKniMuVwj8jCdoI5k8Cb2D5G\nzWGvYFFjYVq+MYNnR5eAGA==\n-----END PRIVATE KEY-----\n",
    "client_email": "firebase-adminsdk-vrq2k@chatrdk-458bf.iam.gserviceaccount.com",
    "client_id": "110910956182788794133",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://accounts.google.com/o/oauth2/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-vrq2k%40chatrdk-458bf.iam.gserviceaccount.com"
  }),
  databaseURL: "https://chatrdk-458bf.firebaseio.com"
});


//defaultDatabase = admin.database();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
/*
 exports.helloWorld = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase!");
 });
*/

exports.serveUser = functions.database.ref('/chatReq/{newU}').onCreate((event) => {
    var db = admin.database();
    var uLon = event.data.val().lon;
    var uLat = event.data.val().lat;
    var uRange = event.data.val().range;
    /*
    db.ref("lat/" + event.data.key).once('value', function (snap) {
        console.log(snap.val());
        locLat = snap;
   }).then(function() {
     console.log(locLat);
     console.log("love");
   });
   ;*/
    // Attach an asynchronous callback to read the data at our posts reference
    //db.ref("locationZ").startAt('500').endAt('1500').on("value", function(snapshot)
    db.ref("locationZ").on("child_added", function(snapshot) {
      var d = getDistanceFromLatLonInKm(snapshot.val().lat, snapshot.val().lon, uLat, uLon);
      //console.log(d);
      if (d <= uRange) {
          console.log(snapshot.key);
      }

    });

    /*

    */
    var ref = db.ref("chatReq/" + event.data.key);
    return ref.set("fixed!!!!");
});

function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
  var R = 6371; // Radius of the earth in km
  var dLat = deg2rad(lat2-lat1);  // deg2rad below
  var dLon = deg2rad(lon2-lon1);
  var a =
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ;
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  var d = R * c; // Distance in km
  return d;
}

function deg2rad(deg) {
  return deg * (Math.PI/180)
}
