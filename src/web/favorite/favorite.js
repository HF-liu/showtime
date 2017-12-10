$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    var offset = 0;
    var count = 20;
    var total = -1;

    window.onload= onLoadFunction();
    $("#favRow").hide();
    if(isAdmin == "false"){
        $(".justAdminall").hide();
    }
    function onLoadFunction(){
        //alert("Under Construction");
    }
    $("#getall").click(function (e) {
        e.preventDefault();
        $("#favTable").find(".cloned").remove();
        jQuery.ajax ({
            url:  "/api/favs",
            type: "GET",
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                data.content.forEach(function(item){
                    $( "#favRow" ).clone().prop("id",item.favId).appendTo( "#favTable");
                    getUserName(item.userId);
                    $("#"+item.favId).find("#UserName").text(localStorage.getItem("temp"));
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
                    // if(isAdmin == "false"){
                    //     $(".justAdmin").hide();
                    // }
                });

            })
            .fail(function(data){
            });
    });

    $("#getmy").click(function (e) {
        e.preventDefault();
        $("#favTable").find(".cloned").remove();
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
                    getUserName(item.userId);
                    $("#"+item.favId).find("#UserName").text(localStorage.getItem("temp"));
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
                    // if(isAdmin == "false"){
                    //     $(".justAdmin").hide();
                    // }
                });

            })
            .fail(function(data){
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

})