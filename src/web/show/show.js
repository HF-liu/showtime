$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    $('#resourceTable').hide();

    //$('#resourceTable').show();

    jQuery.ajax({
        url: "/api/shows",
        type: "GET"
    }).done(function (data) {
        var dataList = data.content;
        //alert(dataList.length);
        var showIdList = new Array();
        for (var i = 0; i < dataList.length; i++) {
            //alert(i);
            var showName = dataList[i].showName;
            var showId = dataList[i].showId;
            showIdList[i] = showId;
            var imgSrc = dataList[i].showphoto;
            //alert("showId is:" + showId);
            var aId = 'show' + i;
            var showDiv = '<li>' +
                '<div>' +
                    '<a href="#">' +
                        '<img src="'+imgSrc+'">' +
                    '</a>' +
                '</div>'+
                '<div>' +
                    '<h2>' +
                        '<a id="'+aId+'">' +
                            showName +
                        '</a>' +
                    '</h2>' +
                '</div>'+
                '</li>'
            //alert(showDiv);
            $('#showRow').append(showDiv);

        }

        function insertTable(showId){
            $('#resourceTable').show();
            jQuery.ajax({

                url: "/api/shows/" + showId,
                // url: "/api/shows/" + $('#show0').value();
                type: "GET"
            }).done(function (data) {
                var content = data.content;
                //alert("channel:" + content.channelId + "intro:" + content.intro + "showCategory:" + content.showCategory);
                $('#channelId').text(content.channelId);
                $('#intro').text(content.intro);
                $('#showCategory').text(content.showCategory);
                $('#showName').text(content.showName);
                $('#showRating').text(content.showRating);

            })
        }

        $('#show0').click(function () {
            insertTable(showIdList[0]);
            /*
            jQuery.ajax({

                url: "/api/shows/" + showIdList[0],
                type: "GET"
            }).done(function (data) {
                var content = data.content;
                alert("channel:" + content.channelId + "intro:" + content.intro + "showCategory:" + content.showCategory);
            })
            */

        })
        $('#show1').click(function () {
            insertTable(showIdList[1]);
            /*
            $('#resourceTable').show();
            jQuery.ajax({

                url: "/api/shows/" + showIdList[1],
                type: "GET"
            }).done(function (data) {
                var content = data.content;
                //alert("channel:" + content.channelId + "intro:" + content.intro + "showCategory:" + content.showCategory);
                $('#channelId').text(content.channelId);
                $('#intro').text(content.intro);
            })
            */


        })

        $('#show2').click(function () {
            insertTable(showIdList[2]);
        })
        $('#show3').click(function () {
            insertTable(showIdList[3]);
        })
        $('#show4').click(function () {
            insertTable(showIdList[4]);
        })
        $('#show5').click(function () {
            insertTable(showIdList[5]);
        })
        $('#show6').click(function () {
            insertTable(showIdList[6]);
        })
        $('#show7').click(function () {
            insertTable(showIdList[7]);
        })
        $('#show8').click(function () {
            insertTable(showIdList[8]);
        })
        $('#show9').click(function () {
            insertTable(showIdList[9]);
        })
    })

})



