const functions = require('firebase-functions');

var admin = require("firebase-admin");

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

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
/*
 exports.helloWorld = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase!");
 });
*/

exports.testUptime = functions.database.ref('/eugine/').onCreate((event) => {
    var db = admin.database()
    var i = 0;
    db.ref('/euginie/').set("" + i)

});

exports.serveUser = functions.database.ref('/chatReq/{newU}').onUpdate((event) => {
    // preload the info from the requested node and create necessary vars
    var db = admin.database();
    var uLon = event.data.val().lon;
    var uLat = event.data.val().lat;
    var uRange = event.data.val().range;
    var i_list = event.data.val().preferences;

    console.log(i_list);
    var eligibleUsers = [];
    var finalList = [];
    eligibleUsers.push(event.data.key);
    var p;
    var m = new Map();

    // get all locations for users which are available for search
    db.ref("searchpool").once('value').then(function(snapshot) {
        p = snapshot.val();
        // iterate through the list, validate the distance and generate the
        // eligible list
        for (var key in p) if (p.hasOwnProperty(key) && key != event.data.key) {
            var d = getDistanceFromLatLonInKm(p[key].lat, p[key].lon, uLat, uLon);

            if (d <= uRange) {
                var s_list = p[key].preferences;
                m.set(key, s_list);
                //aconsole.log (key + "|" + p[key]);
                eligibleUsers.push(key);
            }
        }
        var match_res = match(db, i_list, m);
        match_res[0].push(event.data.key);
        console.log(match_res[0]);
        console.log(match_res[1]);

        // save generated users in a node, AKA creating new chat room
        var k = db.ref("group/").push().key;
        db.ref("group/" + k).child('member').set(match_res[0]);
        db.ref("group/" + k).child('preferences').set(match_res[1]);
        //return ref.set(match_res[0]);
        //console.log (finalList.length + "|" + eligibleUsers.length);


    //var ref = db.ref("chatReq/" + event.data.key);
    //return ref.remove();
    });

});
/*
################# MATCHING ALGO LOGIC #######################
active_search           |               passive_search
-------------------------------------------------------------
-chat_req               |                 search_pool
-seach_pool             |

if (added(chat_req)) {
    candidates_list = distance_filter(search_pool);
    find_match(candidates_list) {
        match_list = matches();
        create_room(match_list);
        decrease_counters(match_list);
        if (counter == 0) { ..create new node to namdle it? idk
            remove_from_pool(user);
        }
        if (in_chat_req(match_list)) {
            remove_from_chat_req(match_list);
        }
    }
}

*/

function match(db, i_list , u_list) {
    // create connectivity matrix 5xn
    var f = new Array();
    var u_size = u_list.size;
    var uid = new Array();
    var bin = new Array();
    var ic = 0;
    u_list.forEach(function (item, key, mapObj) {
        uid[ic] = key;
        var str = "";
        for (j = 0; j < i_list.length; j++) {
            if (item.indexOf(i_list[j]) == -1) {
                str+="0";
            } else {
                str+= "1";
            }
        }
        //console.log(str);
        bin[ic] = str;
        ic++;
        //document.write(item.toString() + "<br />");
    });
    var sorted_bin = bin;
    //console.log(bin);
    //console.log(uid);
    sorted_bin.sort(function(a,b) {
        if (a > b) {
            if (numberOfOnes(a) > numberOfOnes(b)) {
                return -1;
            }
            return 1;
        }
        if (a < b) {
            if (numberOfOnes(a) < numberOfOnes(b)) {
                return 1;
            }
            return -1;
        }
        // a must be equal to b
        return 0;
    });
    var com_c = 1;
    var temp_i;
    var match_str;
    var results = new Array();
    console.log(sorted_bin);
    results.push(sorted_bin[0]);
    match_str = sorted_bin[0];
    for (var i = 0; i < sorted_bin.length - 1; ++i) {
        if ( sorted_bin[i + 1] == sorted_bin[i]) {
            results.push(sorted_bin[i]);
            com_c++;
            match_str = sorted_bin[i];
            //console.log("x" + sorted_bin[i] + "|" + com_c);
        } else {
            com_c = 1;
            var results = new Array();
            results.push(sorted_bin[i]);
        }
        // massive break logic
        var int_count = numberOfOnes(match_str);
        if (com_c >= 4 && int_count >= 4) {
            break;
        } else if (com_c >= 4 && int_count >= 4) {
            break;
        } else if (com_c >= 4 && int_count >= 3) {
            break;
        } else if (com_c >= 3 && int_count >= 4) {
            break;
        } else if (com_c >= 3 && int_count >= 2) {
            break;
        } else if (com_c >= 2 && int_count >= 3) {
            break;
        } else if (com_c >= 1 && int_count >= 2) {
            break;
        } else {

        }
    }

    var final_id = new Array();
    var final_il = new Array();

    for (i = 0; i < results.length; ++i) {
        var in_del = bin.indexOf(results[i]);
        final_id.push(uid[in_del]);
        bin.splice(in_del, 1);
        uid.splice(in_del, 1);
    }
    //console.log(numberOfOnes(match_str));
    for (i = 0; i < match_str.length; ++i) {
        if (match_str[i] == '1') {
            final_il.push(i_list[i]);
        }
    }
    var comb = new Array();
    comb[0] = final_id;
    comb[1] = final_il;

    //console.log(comb[0]);
    //console.log(comb[1]);
    return comb;
}

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
};

// get number of 1's in a string
function numberOfOnes(a) {
    //var count = result.split(1);//  count -1 is your answer
    return ((a.split('1').length-1));

}
