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
    });
});


function addUser() {
    d = [{
        "userName": "David",
        "email": "david.liu2017@sv.cmu.edu",
        "phone": "6508501498",
        "profilePhoto": "345.34.23.6/uyy/xx/avatar.jpg",
        "favs": "Modern Family, Boston Legal",
        "showNum": 2,
        "reviews": "1243456id, 7654321id",
        "friends": "davidid",
        "joinDate": "10/10/2017",
        "password": "2doHGUrLJCW6FiYS1JLDLA=="
    },
        {
            "userName": "Hubert",
            "email": "hubert.jia@sv.cmu.edu",
            "phone": "6509997777",
            "profilePhoto": "345.34.23.6/df23/x65/avatar.jpg",
            "favs": "Empire, Vinyl, Scream Queens",
            "showNum": 3,
            "reviews": "blablablaid, bla2bla2bla2id, rightrightrightid",
            "friends": "hubertid",
            "joinDate": "10/09/2017",
            "password": "qk822KwB1+RlmgJA5NPulg=="
        }];
    var users = dbConnection.collection('users');
    users.insertOne(d[0], function(err,doc){
        if (err){
            console.log("Could not add user 1");
        }
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

showList = ['s1','s2','s3','s4','s5'];
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

    c.forEach(function(review){
        var cars = dbConnection.collection('shows');
        cars.insertOne(review);
    })

}

function addReview(){
    var users = dbConnection.collection('users');
    var shows = dbConnection.collection('shows');
    var reviews = dbConnection.collection('reviews');

    var query1 = { userName: "David" };
    var query2 = { userName: "Hubert" };

    var userid1 = db.collection("users").find(query1, {})._id;
    var userid2 = db.collection("users").find(query2, {})._id;

    var date = new Date();
    date.setFullYear(2020, 0, 14);

    d = [{
        "userId": userid1,
        "showId": userid1,
        "createDate": date,
        "reviewTopic": "345.34.23.6/uyy/xx/avatar.jpg",
        "reviewContent": "Modern Family, Boston Legal"
    },
        {
            "userId": userid2,
            "showId": userid2,
            "createDate": date,
            "reviewTopic": "345.34.23.6/uyy/xx/avatar.jpg",
            "reviewContent": "Modern Family, Boston Legal"
        }];

}



setTimeout(closeConnection,5000);