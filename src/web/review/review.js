$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    var offset = 0;
    var count = 20;
    var total = -1;

    window.onload= onLoadFunction();
    $("#reviewRow").hide();
    function onLoadFunction(){
        // alert(isAdmin);
    }
    $("#getall").click(function (e) {
        e.preventDefault();
        $("#reviewTable").find(".cloned").remove();
        jQuery.ajax ({
            url:  "/api/reviews",
            type: "GET",
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
                    $("#"+item.reviewId).prop("class","cloned");
                    $("#"+item.reviewId).show();
                });
            })
            .fail(function(data){
                $("#carlist").text("Sorry no cars");
            })
    });

    $("#getmy").click(function (e) {
        e.preventDefault();
        $("#reviewTable").find(".cloned").remove();
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
                    $("#"+item.reviewId).prop("class","cloned");
                    $("#"+item.reviewId).show();
                });
            })
            .fail(function(data){
                $("#carlist").text("Sorry no cars");
            })
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
            })
            .fail(function(data){
            })
    }
})