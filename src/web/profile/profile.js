$(function() {
    var token = localStorage.getItem("token");
    var userId = localStorage.getItem("userId");
    var isAdmin = localStorage.getItem("isAdmin");
    var offset = 0;
    var count = 20;
    var total = -1;

    window.onload= onLoadFunction();

    function onLoadFunction(){
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

            })
            .fail(function(data){
            });
    }

    $('#editinfobtn').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget) // Button that triggered the modal
        var username = $('#username').val();
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        var modal = $(this)
        modal.find('.modal-title').text('Now you can edit this review');
        // modal.find('.modal-body input').val(topic);
        alert(username);
        $('#usernameinput').val(username);
        $('#contenttext').val(content);
        // var editedtopic = modal.find('.modal-body input').text();
        // var editedcontent = modal.find('.modal-body textarea').text();
        $("#updatebtn").click(function (e) {
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
                    $("#closethis").trigger("click");
                })
                .fail(function(data){
                    alert("Failed to edit!");
                })
        });
    });


})