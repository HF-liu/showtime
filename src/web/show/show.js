$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    $('#resourceTable').hide();
    $('#reviewTable').hide();
    $("#reviewRow").hide();
    //$('#resourceTable').show();
    function onLoadFunction(){
        if(token == null){
            alert("Not login.");
        }
    }
    jQuery.ajax({
        url: "/api/shows",
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader ("Authorization", token);
        }
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
                    '<h4>' +
                        '<a id="'+aId+'">' +
                            showName +
                        '</a>' +
                    '</h4>' +
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
                type: "GET",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader ("Authorization", token);
                }
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

        function insertRevTable(showId){
            $('#reviewTable').show();
            jQuery.ajax({

                url: "/api/shows/" + showId + "/reviews",
                type: "GET",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader ("Authorization", token);
                }
            }).done(function (data) {
                data.content.forEach(function(item){
                    $( "#reviewRow" ).clone().prop("id",item.reviewId).appendTo( "#reviewTable");
                    console.log($("#"+item.reviewId))
                    $("#"+item.reviewId).find("#reviewTopic").text(item.reviewTopic);
                    $("#"+item.reviewId).find("#reviewContent").text(item.reviewContent);


                    var btn = document.createElement("Button");
                    var t = document.createTextNode("Delete");
                    btn.appendChild(t);
                    btn.type = "button";
                    btn.className = "deletereviewbtn btn btn-primary btm-sm btn-default";
                    btn.style = "margin-right:10px";

                    var btn1 = document.createElement("Button");
                    var t = document.createTextNode("Edit");
                    btn1.appendChild(t);
                    btn1.type = "button";
                    btn.id = "editButton"
                    btn1.className = "editreviewbtn btn btn-primary btm-sm ";
                    btn1.setAttribute("data-toggle", "modal");
                    btn1.setAttribute("data-target", "#editReview");
                    btn1.setAttribute("data-userId", item.userId);

                    // var btn = document.createElement("BUTTON");
                    // var t = document.createTextNode("CLICK ME");
                    // btn.appendChild(t);
                    if ($("#"+item.reviewId).find("#Operations")[0].querySelector("#editButton")== null) {
                        $("#" + item.reviewId).find("#Operations")[0].appendChild(btn);
                        $("#" + item.reviewId).find("#Operations")[0].appendChild(btn1);
                    }
                    $("#"+item.reviewId).prop("class","cloned");
                    $("#"+item.reviewId).show();
                });

            })
                .fail(function(data){
                });

                    // $("#Operations").append(btn);
                    // $("#Operations").append(btn1);

                    $('#editReview').on('show.bs.modal', function (event) {
                        var button = $(event.relatedTarget) // Button that triggered the modal
                        var reviewId = button.parents("tr").attr("id");
                        var topic = button.parents("tr").find("#reviewTopic").text();
                        var content = button.parents("tr").find("#reviewContent").text();
                        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
                        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
                        var modal = $(this)
                        modal.find('.modal-title').text('Now you can edit this review');
                        // modal.find('.modal-body input').val(topic);
                        $('#topictext').val(topic);
                        $('#contenttext').val(content);
                        // var editedtopic = modal.find('.modal-body input').text();
                        // var editedcontent = modal.find('.modal-body textarea').text();
                        $("#updatereview").click(function (e) {
                            jQuery.ajax({
                                url: "/api/reviews/" + reviewId,
                                type: "PATCH",
                                data: JSON.stringify({
                                    reviewTopic: $('#topictext').val(),
                                    reviewContent: $('#contenttext').val()
                                }),
                                dataType: "json",
                                contentType: "application/json; charset=utf-8",
                                beforeSend: function (xhr) {
                                    xhr.setRequestHeader("Authorization", token);
                                }
                            })
                                .done(function (data) {
                                    $("#closereview").trigger("click");
                                    location.reload();
                                })
                                .fail(function (data) {
                                    alert("Failed to edit!");
                                })
                        });
                    });

            $("body").on('click','.deletereviewbtn',function(){
                // var id=$(this).parents("tr").find("#Arthur").text();
                // alert(id);

                // var reviewid = $(this).parents("tr").id;
                var reviewid = $(this).parents("tr").attr("id");
                console.log(reviewid);
                // alert(reviewid);
                jQuery.ajax ({
                    url:  "/api/reviews/"+reviewid,
                    type: "DELETE",
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader ("Authorization", token);
                    }
                })
                    .done(function(data){
                        alert("Successfully deleted!");
                        location.reload();
                    })
                    .fail(function(data){
                        alert("Failed to detele.");
                    });

            });

        }
        for (var j = 0; j < dataList.length; j++) {
            $('#show' + j).click(createCallback(j,showIdList))
        }


        function createCallback(j,showIdList){
            return function(){
                insertTable(showIdList[j]);
                insertRevTable(showIdList[j]);
            }
        }


        //
        // $('#show0').click(function () {
        //     insertTable(showIdList[0]);
        //     insertRevTable(showIdList[0]);
        // })
        // $('#show1').click(function () {
        //     insertTable(showIdList[1]);
        //     insertRevTable(showIdList[1]);
        // })
        //
        // $('#show2').click(function () {
        //     insertTable(showIdList[2]);
        //     insertRevTable(showIdList[2]);
        // })
        // $('#show3').click(function () {
        //     insertTable(showIdList[3]);
        //     insertRevTable(showIdList[3]);
        // })
        // $('#show4').click(function () {
        //     insertTable(showIdList[4]);
        //     insertRevTable(showIdList[4]);
        // })
        // $('#show5').click(function () {
        //     insertTable(showIdList[5]);
        //     insertRevTable(showIdList[5]);
        // })
        // $('#show6').click(function () {
        //     insertTable(showIdList[6]);
        //     insertRevTable(showIdList[6]);
        // })
        // $('#show7').click(function () {
        //     insertTable(showIdList[7]);
        //     insertRevTable(showIdList[7]);
        // })
        // $('#show8').click(function () {
        //     insertTable(showIdList[8]);
        //     insertRevTable(showIdList[8]);
        // })
        // $('#show9').click(function () {
        //     insertTable(showIdList[9]);
        //     insertRevTable(showIdList[9]);
        // })
        // $('#show10').click(function () {
        //     insertTable(showIdList[10]);
        //     insertRevTable(showIdList[10]);
        // })
        // $('#show11').click(function () {
        //     insertTable(showIdList[11]);
        //     insertRevTable(showIdList[11]);
        // })
        // $('#show12').click(function () {
        //     insertTable(showIdList[12]);
        //     insertRevTable(showIdList[12]);
        // })
    })

})



