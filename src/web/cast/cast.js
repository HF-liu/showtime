$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    $('#resourceTable').hide();

    //$('#resourceTable').cast();


    jQuery.ajax({
        url: "/api/casts",
        type: "GET"
    }).done(function (data) {
        var dataList = data.content;
        //alert(dataList.length);
        var castIdList = new Array();
        for (var i = 0; i < dataList.length; i++) {
            //alert(i);
            var castName = dataList[i].castName;
            var castId = dataList[i].castId;
            castIdList[i] = castId;
            var imgSrc = dataList[i].castPhoto;
            //alert("castId is:" + castId);
            var aId = 'cast' + i;
            var castDiv = '<li>' +
                '<div>' +
                    '<a href="#">' +
                        '<img src="'+imgSrc+'">' +
                    '</a>' +
                '</div>'+
                '<div>' +
                    '<h2>' +
                        '<a id="'+aId+'">' +
                            castName +
                        '</a>' +
                    '</h2>' +
                '</div>'+
                '</li>'
            //alert(castDiv);
            $('#castRow').append(castDiv);

        }

        function insertTable(castId){
            $('#resourceTable').cast();
            jQuery.ajax({

                url: "/api/casts/" + castId,
                type: "GET"
            }).done(function (data) {
                var content = data.content;
                //alert("channel:" + content.channelId + "intro:" + content.intro + "castCategory:" + content.castCategory);
                $('#showId').text(content.showId);
                $('#castName').text(content.castName);
                $('#roles').text(content.roles);


            })
        }

        $('#cast0').click(function () {
            insertTable(castIdList[0]);
            /*
            jQuery.ajax({

                url: "/api/casts/" + castIdList[0],
                type: "GET"
            }).done(function (data) {
                var content = data.content;
                alert("channel:" + content.channelId + "intro:" + content.intro + "castCategory:" + content.castCategory);
            })
            */

        })
        $('#cast1').click(function () {
            insertTable(castIdList[1]);
            /*
            $('#resourceTable').cast();
            jQuery.ajax({

                url: "/api/casts/" + castIdList[1],
                type: "GET"
            }).done(function (data) {
                var content = data.content;
                //alert("channel:" + content.channelId + "intro:" + content.intro + "castCategory:" + content.castCategory);
                $('#channelId').text(content.channelId);
                $('#intro').text(content.intro);
            })
            */


        })

        $('#cast2').click(function () {
            insertTable(castIdList[2]);
        })
        $('#cast3').click(function () {
            insertTable(castIdList[3]);
        })
        $('#cast4').click(function () {
            insertTable(castIdList[4]);
        })
        $('#cast5').click(function () {
            insertTable(castIdList[5]);
        })
        $('#cast6').click(function () {
            insertTable(castIdList[6]);
        })
        $('#cast7').click(function () {
            insertTable(castIdList[7]);
        })
        $('#cast8').click(function () {
            insertTable(castIdList[8]);
        })
        $('#cast9').click(function () {
            insertTable(castIdList[9]);
        })
    })

})



