$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    var offset = 0;
    var count = 20;
    var total = -1;

    window.onload= onLoadFunction();
    $("#calRow").hide();
    if(isAdmin == "false"){
        $(".justAdmin").hide();
    }
    function onLoadFunction(){
        //alert("Under Construction");
        // alert(isAdmin);

    }
    $("#getall").click(function (e) {
        e.preventDefault();
        $("#calTable").find(".cloned").remove();
        jQuery.ajax ({
            url:  "/api/calendars",
            type: "GET",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                data.content.forEach(function(item){
                    $( "#calRow" ).clone().prop("id",item.calendarId).appendTo( "#calTable");
                    // getUserName(item.userId);
                    // $("#"+item.reviewId).find("#Arthur").text(localStorage.getItem("temp"));
                    // getShowName(item.showId);
                    // $("#"+item.reviewId).find("#Show").text(localStorage.getItem("temp"));
                    $("#"+item.calendarId).find("#Date").text(item.date);
                    $("#"+item.calendarId).find("#Event").text(item.event);


                    var btn = document.createElement("Button");
                    var t = document.createTextNode("Delete");
                    btn.appendChild(t);
                    btn.type = "button";
                    btn.className = "deletebtn btn btn-primary btm-sm btn-default";
                    btn.style = "margin-right:10px";

                    var btn1 = document.createElement("Button");
                    var t = document.createTextNode("Edit");
                    btn1.appendChild(t);
                    btn1.type = "button";
                    btn1.className = "btn btn-primary btm-sm ";
                    btn1.setAttribute("data-toggle", "modal");
                    btn1.setAttribute("data-target", "#editEvent");
                    btn1.setAttribute("data-userId", item.userId);

                    // var btn = document.createElement("BUTTON");
                    // var t = document.createTextNode("CLICK ME");
                    // btn.appendChild(t);
                    $("#"+item.calendarId).find("#Operations")[0].appendChild(btn);
                    $("#"+item.calendarId).find("#Operations")[0].appendChild(btn1);

                    $("#"+item.calendarId).prop("class","cloned");
                    $("#"+item.calendarId).show();
                    if(isAdmin == "false"){
                        $(".justAdmin").hide();
                    }

                });
            })
            .fail(function(data){
            })
    });

    $("#getmy").click(function (e) {
        e.preventDefault();
        $("#calTable").find(".cloned").remove();
        jQuery.ajax ({
            url:  "/api/users/"+userId+"/calendars",
            type: "GET",
            async: false,
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                data.content.forEach(function(item){
                    $( "#calRow" ).clone().prop("id",item.calendarId).appendTo( "#calTable");
                    // getUserName(item.userId);
                    // $("#"+item.reviewId).find("#Arthur").text(localStorage.getItem("temp"));
                    // getShowName(item.showId);
                    // $("#"+item.reviewId).find("#Show").text(localStorage.getItem("temp"));
                    $("#"+item.calendarId).find("#Date").text(item.date);
                    $("#"+item.calendarId).find("#Event").text(item.event);

                    var btn = document.createElement("Button");
                    var t = document.createTextNode("Delete");
                    btn.appendChild(t);
                    btn.type = "button";
                    btn.className = "deletebtn1 btn btn-primary btm-sm";
                    btn.style = "margin-right:10px";

                    var btn1 = document.createElement("Button");
                    var t = document.createTextNode("Edit");
                    btn1.appendChild(t);
                    btn1.type = "button";
                    btn1.className = "btn btn-primary btm-sm ";
                    btn1.setAttribute("data-toggle", "modal");
                    btn1.setAttribute("data-target", "#editEvent");
                    btn1.setAttribute("data-userId", item.userId);

                    // var btn = document.createElement("BUTTON");
                    // var t = document.createTextNode("CLICK ME");
                    // btn.appendChild(t);

                    $("#"+item.calendarId).find("#Operations")[0].appendChild(btn);
                    $("#"+item.calendarId).find("#Operations")[0].appendChild(btn1);

                    $("#"+item.calendarId).prop("class","cloned");
                    $("#"+item.calendarId).show();

                    if(isAdmin == "false"){
                        $(".justAdmin").hide();
                    }

                    // $(".deletebtn1").hide();
                });
            })
            .fail(function(data){
            })
    });

    $("body").on('click','.deletebtn',function(){
        // var id=$(this).parents("tr").find("#Arthur").text();
        // alert(id);

        // var reviewid = $(this).parents("tr").id;
        var calid = $(this).parents("tr").attr("id");
        // alert(reviewid);
        jQuery.ajax ({
            url:  "/api/calendars/"+calid,
            type: "DELETE",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                alert("Successfully deleted!");
                $("#getall").trigger("click");
            })
            .fail(function(data){
                alert("Failed to detele.");
            })

    });

    $("body").on('click','.deletebtn1',function(){
        // var id=$(this).parents("tr").find("#Arthur").text();
        // alert(id);

        // var reviewid = $(this).parents("tr").id;
        var calid = $(this).parents("tr").attr("id");
        // alert(reviewid);
        jQuery.ajax ({
            url:  "/api/calendars/"+calid,
            type: "DELETE",
            beforeSend: function (xhr) {
                xhr.setRequestHeader ("Authorization", token);
            }
        })
            .done(function(data){
                alert("Successfully deleted!");
                $("#getmy").trigger("click");
            })
            .fail(function(data){
                alert("Failed to detele.");
            })

    });

    $("#editEvent").on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var calid = button.parents("tr").attr("id");
        var date = button.parents("tr").find("#Date").text();
        var eventcontent = button.parents("tr").find("#Event").text();
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-title').text('Now you can edit this event');
        // modal.find('.modal-body input').val(topic);
        $('#recipient-name').val(date);
        $('#message-text').val(eventcontent);
        // var editedtopic = modal.find('.modal-body input').text();
        // var editedcontent = modal.find('.modal-body textarea').text();
        $("#updatebtn").click(function (e) {
            jQuery.ajax ({
                url:  "/api/calendars/"+calid,
                type: "PATCH",
                data: JSON.stringify({date:$('#recipient-name').val(), event: $('#message-text').val()}),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader ("Authorization", token);
                }
            })
                .done(function(data){
                    $("#closethis").trigger("click");
                })
                .fail(function(data){
                    alert("Failed to edit!");
                })
        });
    });


    $("#postnew").on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        // var calid = button.parents("tr").attr("id");
        // var date = button.parents("tr").find("#Date").text();
        // var eventcontent = button.parents("tr").find("#Event").text();
        alert("here it is!");
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-title').text('Now you can edit this event');
        // modal.find('.modal-body input').val(topic);
        // $('#recipient-name').val(date);
        // $('#message-text').val(eventcontent);
        // var editedtopic = modal.find('.modal-body input').text();
        // var editedcontent = modal.find('.modal-body textarea').text();
        $("#updatecontent").click(function (e) {
            jQuery.ajax ({
                url:  "/api/calendars/"+calid,
                type: "PATCH",
                data: JSON.stringify({date:$('#recipient-name').val(), event: $('#message-text').val()}),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader ("Authorization", token);
                }
            })
                .done(function(data){
                    $("#closewindow").trigger("click");
                })
                .fail(function(data){
                    alert("Failed to edit!");
                })
        });
    });

})