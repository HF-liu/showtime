var MongoClient = require('mongodb').MongoClient;

var dbConnection = null;

var lockCount = 0;



function getDbConnection(callback){
    MongoClient.connect("mongodb://localhost/app17-5", function(err, db){
        if(err){
            console.log("Unable to connect to Mongodb");
        }else{
            dbConnection = db;
            callback();
        }
    });
};

function closeConnection() {
    if (dbConnection)
        dbConnection.close();

}

getDbConnection(function(){
    dbConnection.dropDatabase(function(err,doc){
        if (err)
            console.log("Could not drop database");
        else
            addUser();
            addShow();
            addReview();
            addCast();
    });
});


function addUser() {
    d = [{
        "userName": "David",
        "email": "david.liu2017@sv.cmu.edu",
        "password": "2doHGUrLJCW6FiYS1JLDLA=="
    },
        {
            "userName": "Hubert",
            "email": "hubert.jia@sv.cmu.edu",
            "password": "qk822KwB1+RlmgJA5NPulg=="
        }];
    var users = dbConnection.collection('users');
    users.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add user 1");
        }
        addAdmin(doc.ops[0]._id.toString());
        // else {
        //     addReviewstoUser(doc.ops[0]._id.toString(),100);
        // }
    })
    users.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add user 2");
        }
        // else {
        //     addReviewstoUser(doc.ops[0]._id.toString(),120);
        // }
    })
}
function addAdmin(ID){
    var admins = dbConnection.collection('admins');


    d = [{
        "userId":ID
    }];

    admins.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add admin 1");
        }
    })
}

showList = ['s1','s2'];
channelList = ['c1','c2'];
introList = ['Long long ago','Long ago','Not too long ago','Last year','Last month','Last week','Yesterday','Several hours ago','Just now'];
categoryList = ['Awful','Disgusting','Too bad','Worst', 'Not recommend', 'Waste of time','Not too bad', 'Just fine', 'Could be better', 'So cool', 'Hilarious','Amazing','Best ever', 'Watched it twice'];
photoList = ['url1','url2','url3','url4'];


function addShow() {
    sequence = Array(10);
    console.log("sequence",sequence);
    var c = [];
    for (i=0;i<10;i++){
        console.log("Trying")
        var showName = showList[Math.floor(Math.random()*showList.length)];
        var channelId = channelList[Math.floor(Math.random()*channelList.length)];
        var intro = introList[Math.floor(Math.random()*introList.length)];
        var showCategory = categoryList[Math.floor(Math.random()*categoryList.length)];
        var showphoto = photoList[Math.floor(Math.random()*photoList.length)];
        var showRating = Number(Math.floor(Math.random()*5));


        c.push ({
            showName: showName,
            channelId: channelId,
            intro: intro,
            showCategory: showCategory,
            showphoto: showphoto,
            showRating: showRating
        });

    }


    c.forEach(function(show){
        var shows = dbConnection.collection('shows');
        shows.insertOne(show);
        addCast(show._id.toString());
    })

}

function addCast(ID){
    var casts = dbConnection.collection('casts');


    d = [{
        "showId":ID,
        "castName" : "Jim Parsons",
        "roles" : "Sheldon Cooper",
        "castPhoto" : "url10"
    },
        {
            "showId":ID,
            "castName" : "Jesse Tyler Ferguson",
            "roles" : "Mitchell Pritchett",
            "castPhoto" : "url11"
        },
        {
            "showId":ID,
            "castName" : "Lana Parrilla",
            "roles" : "Evil Queen",
            "castPhoto" : "url11"
        }];

    casts.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add cast 1");
        }
    })
    casts.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add cast 2");
        }
    })
    casts.insertOne(d[2], function(err,doc){
        if (err){
            console.log("Could not add cast 3");
        }
    })

}

function addReview(){
    var users = dbConnection.collection('users');
    var shows = dbConnection.collection('shows');
    var reviews = dbConnection.collection('reviews');

    var query1 = { userName: 'David' };
    var query2 = { showName: 's1' };
    var query3 = { userName: 'Hubert' };
    var query4 = { showName: 's1' };

    var userid2;
    var userid1;

    var result;

    users.find(query1).toArray(function(err, result) {
        if (err) throw err;
        shows.find(query2).toArray(function(error, res) {
            addtest(result[0]._id,res[0]._id);
        });
    });

    users.find(query3).toArray(function(err, result) {
        if (err) throw err;
        shows.find(query4).toArray(function(error, res) {
            addtest2(result[0]._id,res[0]._id);
        });
    });


    users.find(query3).toArray(function(err, result) {
        if (err) throw err;
        userid1 = result[0]._id;

    });

// _id    console.log("test");
//     console.log(userid1);
//     console.log(userid2);
//
    var date = new Date(2020,0,14);



}

function addtest(ID1,ID2) {
    console.log(ID1);
    console.log(ID2);

    var favs = dbConnection.collection('favs');
    var reviews = dbConnection.collection('reviews');

    var date = new Date(2020, 0, 14);

    d = [{
        "userId": ID1.toString(),
        "showId": ID2.toString(),
        "createDate": date,
        "reviewTopic": "345.34.23.6/uyy/xx/avatarb.jpg",
        "reviewContent": "Modern Family, Boston Legal"
    },
        {
            "userId": ID1.toString(),
            "showId": ID2.toString(),
            "createDate": date,
            "reviewTopic": "345.34.23.6/uyy/xx/avatara.jpg",
            "reviewContent": "Modern Family, Boston Legal"
        }];

    e = [{
        "userId": ID1.toString(),
        "showId": ID2.toString()
    },
        {
            "userId": ID1.toString(),
            "showId": ID2.toString()
        }];

    reviews.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add review 1");
        }
    })
    reviews.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add review 2");
        }
    })

    favs.insertOne(e[0], function(err,doc){
        if (err){
            console.log("Could not add fav 1");
        }
    })
    favs.insertOne(e[1], function(err,doc){
        if (err){
            console.log("Could not add fav 2");
        }
    })

}

function addtest2(ID1,ID2) {
    console.log(ID1);
    console.log(ID2);

    var favs = dbConnection.collection('favs');
    var reviews = dbConnection.collection('reviews');

    var date = new Date();
    date.setFullYear(2020, 0, 14);

    d = [{
        "userId": ID1.toString(),
        "showId": ID2.toString(),
        "createDate": date,
        "reviewTopic": "345.34.23.6/uyy/xx/avatarb.jpg",
        "reviewContent": "Modern Family, Boston Legal"
    },
        {
            "userId": ID1.toString(),
            "showId": ID2.toString(),
            "createDate": date,
            "reviewTopic": "345.34.23.6/uyy/xx/avatara.jpg",
            "reviewContent": "Modern Family, Boston Legal"
        }];


    e = [{
        "userId": ID1.toString(),
        "showId": ID2.toString()
    },
        {
            "userId": ID1.toString(),
            "showId": ID2.toString()
        }];


    reviews.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add review 1");
        }
        // else {
        //     addReviewstoUser(doc.ops[0]._id.toString(),100);
        // }
    })
    reviews.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add review 2");
        }
        // else {
        //     addReviewstoUser(doc.ops[0]._id.toString(),100);
        // }
    })

    favs.insertOne(e[0], function(err,doc){
        if (err){
            console.log("Could not add fav 1");
        }
    })
    favs.insertOne(e[1], function(err,doc){
        if (err){
            console.log("Could not add fav 2");
        }
    })

}


setTimeout(closeConnection,5000);
