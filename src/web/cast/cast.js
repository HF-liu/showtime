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

    });

    $('#addCast').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        // var editedtopic = modal.find('.modal-body input').text();
        // var editedcontent = modal.find('.modal-body textarea').text();
        $("#addCastButton").click(function (e) {
            data = {};
            data["showId"] = $("#showIdInput").val();
            data["castName"] = $("#castNameInput").val();
            data["roles"] = $("#rolesInput").val();
            data["castPhoto"] = $("#castPhotoInput").val();


            jQuery.ajax({
                url: "/api/casts/",
                type: "POST",
                data: JSON.stringify(data),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", token);
                }
            })
                .done(function (data) {
                    $("#closeAdd").trigger("click");
                    location.reload();
                })
                .fail(function (data) {
                    alert("Failed to edit!");
                })
        });
    });

})



