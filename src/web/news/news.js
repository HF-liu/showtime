$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    var offset = 0;
    var count = 20;
    var total = -1;

    window.onload= onLoadFunction();

    function onLoadFunction(){
        if(token == null){
            alert("Not login.");
        }
    }
    jQuery.ajax({
        url: "/api/news",
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader ("Authorization", token);
        }
    }).done(function (data) {
        data.content.forEach(function (item) {
            $("#newsRow").clone().prop("id", item.newsId).appendTo("#newsTable");
            $("#"+item.newsId).find("#Source").text(item.source);
            $("#"+item.newsId).find("#Date").text(item.date);
            $("#"+item.newsId).find("#Title").text(item.title);
            $("#"+item.newsId).find("#Content").text(item.content);
        });
    });

    $('#addNews').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this);
        // var editedtopic = modal.find('.modal-body input').text();
        // var editedcontent = modal.find('.modal-body textarea').text();
        $("#addnewsButton").click(function (e) {
            data = {};
            data["source"] = $("#sourceInput").val();
            data["date"] = $("#dateInput").val();
            data["title"] = $("#titleInput").val();
            data["content"] = $("#contentInput").val();


            jQuery.ajax({
                url: "/api/news/",
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

});