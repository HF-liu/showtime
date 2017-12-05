$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    $('#resourceTable').hide();

    //$('#resourceTable').cast();


    jQuery.ajax({
        url: "/api/casts",
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", token);
        }
    }).done(function (data) {

        var dataList = data.content;
        //alert(dataList.length);
        var castIdList = new Array();
        for (var i = 0; i < dataList.length; i++) {
            //alert(i);
            var castName = dataList[i].castName;
            var roles = dataList[i].roles;
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
                    '<h3>' +
                        '<a id="'+aId+'">' +
                            castName +
                        '</a>' +
                    '</h3>' +
                '</div>'+
                '</li>'
            //alert(castDiv);
            $('#castRow').append(castDiv);

        }


        function insertTable(castId){
            $('#castTable').show();
            jQuery.ajax({
                url: "/api/casts/" + castId,
                type: "GET",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", token);
                }
            }).done(function (data) {
                var content = data.content;//alert("channel:" + content.channelId + "intro:" + content.intro + "showCategory:" + content.showCategory);
                $('#showId').text(content.showId);
                $('#castName').text(content.castName);
                $('#role').text(content.roles);

            }).fail(function(data){
            });
        }

        for (var j = 0; j < dataList.length; j++) {
            $('#cast' + j).click(createCallback(j,castIdList))
        }


        function createCallback(j,castIdList){
            return function(){
                insertTable(castIdList[j]);
            }
        }

    })

})



