$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    var offset = 0;
    var count = 20;
    var total = -1;

    window.onload= onLoadFunction();
    function onLoadFunction(){
        $("#reviewRow").hide();
        $("#favRow").hide();
        // $("#reviewTable").find(".cloned").remove();
        jQuery.ajax ({
            url:  "/api/users/"+userId,
            type: "GET",
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                $("#userid").text(userId);
                $("#username").text(data.content.userName);
                $("#email").text(data.content.email);
                var identity = null;
                if(localStorage.getItem("isAdmin") == "true"){
                    identity = "Admin";
                } else {
                    identity = "User";
                }
                $("#role").text(identity);

            })
            .fail(function(data){
            });

        jQuery.ajax ({
            url:  "/api/users/"+userId+"/reviews",
            type: "GET",
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                data.content.forEach(function(item){
                    $( "#reviewRow" ).clone().prop("id",item.reviewId).appendTo( "#reviewTable");
                    getUserName(item.userId);
                    $("#"+item.reviewId).find("#Arthur").text(localStorage.getItem("temp"));
                    getShowName(item.showId);
                    $("#"+item.reviewId).find("#Show").text(localStorage.getItem("temp"));
                    $("#"+item.reviewId).find("#Topic").text(item.reviewTopic);
                    $("#"+item.reviewId).find("#Content").text(item.reviewContent);


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
                    btn1.className = "editreviewbtn btn btn-primary btm-sm ";
                    btn1.setAttribute("data-toggle", "modal");
                    btn1.setAttribute("data-target", "#editReview");
                    btn1.setAttribute("data-userId", item.userId);

                    // var btn = document.createElement("BUTTON");
                    // var t = document.createTextNode("CLICK ME");
                    // btn.appendChild(t);
                    $("#"+item.reviewId).find("#Operations")[0].appendChild(btn);
                    $("#"+item.reviewId).find("#Operations")[0].appendChild(btn1);

                    $("#"+item.reviewId).prop("class","cloned");
                    $("#"+item.reviewId).show();
                });

            })
            .fail(function(data){
            });

        jQuery.ajax ({
            url:  "/api/users/"+userId+"/favs",
            type: "GET",
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                data.content.forEach(function(item){
                    $( "#favRow" ).clone().prop("id",item.favId).appendTo( "#favTable");

                    getShowName(item.showId);
                    $("#"+item.favId).find("#Show").text(localStorage.getItem("temp"));
                    $("#"+item.favId).find("#ShowIntro").text(localStorage.getItem("intro"));


                    var btn = document.createElement("Button");
                    var t = document.createTextNode("Unfollow");
                    btn.appendChild(t);
                    btn.type = "button";
                    btn.className = "unfollowbtn btn btn-primary btm-sm btn-default";
                    // btn.style = "margin-right:10px";


                    // var btn = document.createElement("BUTTON");
                    // var t = document.createTextNode("CLICK ME");
                    // btn.appendChild(t);
                    $("#"+item.favId).find("#Operations")[0].appendChild(btn);
                    // $("#"+item.favId).find("#Operations")[0].appendChild(btn1);

                    $("#"+item.favId).prop("class","cloned");
                    $("#"+item.favId).show();
                });

            })
            .fail(function(data){
            });

    }

    $("#editinfo").on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var username = $("#username").text();
        var email = $("#email").text();
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-title').text('Now you can edit this review');
        // modal.find('.modal-body input').val(topic);

        $('#usernameinput').val(username);
        $('#useremailinput').val(email);
        // $('#contenttext').val(content);
        // var editedtopic = modal.find('.modal-body input').text();
        // var editedcontent = modal.find('.modal-body textarea').text();
        $("#updatebtn").click(function (e) {
            var pwd1 = $('#passwordinput').val();
            var pwd2 = $('#reenterinput').val();
            if((pwd1 !== pwd2)){
                alert("You should re-enter the same password you want to set.");
            } else {
                var obj = Object();
                obj.userName = $('#usernameinput').val();
                obj.email = $('#useremailinput').val();
                obj.password = "";
                if (pwd1 != "") {
                    obj.password = $('#passwordinput').val();
                } else {
                    delete obj['password'];
                }
                var patchinfo = JSON.stringify(obj);
                // alert(patchinfo);
                jQuery.ajax({
                    url: "/api/users/" + userId,
                    type: "PATCH",
                    data: patchinfo,
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader("Authorization", token);
                    }
                })
                    .done(function (data) {
                        // $("#closethis").trigger("click");
                        // location.reload();
                    })
                    .fail(function (data) {
                        alert("Failed to edit!");
                    })
            }
        });
    });

    function getUserName(userId) {
        jQuery.ajax ({
            url:  "/api/users/"+userId,
            type: "GET",
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                localStorage.setItem("temp",data.content.userName);
            })
            .fail(function(data){
            })
    }

    function getShowName(showId) {
        jQuery.ajax ({
            url:  "/api/shows/"+showId,
            type: "GET",
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                localStorage.setItem("temp",data.content.showName);
                localStorage.setItem("intro",data.content.intro);
            })
            .fail(function(data){
            })
    }

    $('#editReview').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var reviewId = button.parents("tr").attr("id");
        var topic = button.parents("tr").find("#Topic").text();
        var content = button.parents("tr").find("#Content").text();
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
            jQuery.ajax ({
                url:  "/api/reviews/"+reviewId,
                type: "PATCH",
                data: JSON.stringify({reviewTopic:$('#topictext').val(), reviewContent: $('#contenttext').val()}),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader ("Authorization", token);
                }
            })
                .done(function(data){
                    $("#closereview").trigger("click");
                    location.reload();
                })
                .fail(function(data){
                    alert("Failed to edit!");
                })
        });
    });

    $("body").on('click','.unfollowbtn',function(){

        var favid = $(this).parents("tr").attr("id");
        jQuery.ajax ({
            url:  "/api/favs/"+favid,
            type: "DELETE",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                alert("Successfully unfollowed!");
                location.reload();
            })
            .fail(function(data){
                alert("Failed to detele.");
            })

    });

    $("body").on('click','.deletereviewbtn',function(){
        // var id=$(this).parents("tr").find("#Arthur").text();
        // alert(id);

        // var reviewid = $(this).parents("tr").id;
        var reviewid = $(this).parents("tr").attr("id");
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
            })

    });

})