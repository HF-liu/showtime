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
        else {
            addReviewstoUser(doc.ops[0]._id.toString(),100);
        }
    })
    users.insertOne(d[1], function(err,doc){
        if (err){
            console.log("Could not add user 2");
        }
        else {
            addReviewstoUser(doc.ops[0]._id.toString(),120);
        }
    })
}

showIdList = ['s1','s2','s3','s4','s5','s6','s7','s8','s9','s10'];
episodeIdList = ['ep1','ep2','ep3','ep4','ep5','ep6','ep7','ep8','ep9','ep10'];
dateList = ['Long long ago','Long ago','Not too long ago','Last year','Last month','Last week','Yesterday','Several hours ago','Just now'];
topicList = ['Awful','Disgusting','Too bad','Worst', 'Not recommend', 'Waste of time','Not too bad', 'Just fine', 'Could be better', 'So cool', 'Hilarious','Amazing','Best ever', 'Watched it twice'];

function addReviewstoUser(userId,count) {
    sequence = Array(count);
    console.log("sequence",sequence);
    var c = [];
    for (i=0;i<count;i++){
        console.log("Trying")
        var showId = showIdList[Math.floor(Math.random()*showIdList.length)];
        var episodeId = episodeIdList[Math.floor(Math.random()*episodeIdList.length)];
        var rate = Number(Math.floor(Math.random()*10));
        var createDate = dateList[Math.floor(Math.random()*dateList.length)];
        var editDate = dateList[Math.floor(Math.random()*dateList.length)];
        var reviewTopic = topicList[Math.floor(Math.random()*topicList.length)];
        var reviewContent = topicList[Math.floor(Math.random()*topicList.length)];
        var likes = Number(Math.floor(Math.random()*1000));


        c.push ({
            showId: showId,
            episodeId: episodeId,
            rate: rate,
            createDate: createDate,
            editDate: editDate,
            reviewTopic: reviewTopic,
            reviewContent: reviewContent,
            likes: likes,
            userId: userId
        });

    }

    c.forEach(function(review){
        var cars = dbConnection.collection('reviews');
        cars.insertOne(review);
    })

}


setTimeout(closeConnection,5000);