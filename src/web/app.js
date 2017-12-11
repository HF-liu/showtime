$(function() {
    var isAdmin = null;
    var token = null;
    var userId = null;
    var offset = 0;
    var count = 20;
    var total = -1;

    $("#loadcontent").hide();
    // $("#resourceRow").hide();

    $("#signin").click(function (e) {
        $("#loading").show();
        e.preventDefault();
        jQuery.ajax ({
            url:  "/api/sessions",
            type: "POST",
            async: false,
            data: JSON.stringify({email:$("#inputEmail").val(), password: $("#inputPassword").val()}),
            dataType: "json",
            contentType: "application/json; charset=utf-8"
        }).done(function(data){
            // $("#greeting").text("Hello " + data.content.firstName);
            $("#loadcontent").show();
            token = data.content.token;
            userId = data.content.userId;
            localStorage.setItem("token", token);
            localStorage.setItem("userId", userId);
                $.ajax({
                    url:  "/api/admins/"+localStorage.getItem("userId"),
                    type: "GET",
                    async: false,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader ("Authorization", localStorage.getItem("token"));
                    }
                }).done(function(data){
                        if(data.content == null){
                            localStorage.setItem("isAdmin",false);
                        }else{
                            localStorage.setItem("isAdmin",true);
                        }
                    }).fail(function(data){
                        localStorage.setItem("isAdmin",false);
                    })
            location.href = "show/show.html"
        }).fail(function(data){
                alert("Wrong userid or password. Please try again.");
            })
    })
//
//     $("#loadcontent").click(function (e) {
//         e.preventDefault();
//         loadReview();
//     });
//
//     $("#next").click(function(e){
//         e.preventDefault();
//         if (offset+count < total) {
//             offset = offset+count;
//             loadReview();
//         }
//     })
//
//     $("#previous").click(function(e){
//         e.preventDefault();
//         console.log("Cliked")
//         if (offset-count >= 0) {
//             offset = offset-count;
//             loadReview();
//
//         }
//     })

    // function loadReview() {
    //     jQuery.ajax ({
    //         url:  "/api/users/" + userId + "/reviews?sort=rate&offset=" + offset + "&count="  + count,
    //         type: "GET",
    //         beforeSend: function (xhr) {
    //             xhr.setRequestHeader ("Authorization", token);
    //         }
    //     })
    //         .done(function(data){
    //             total = data.metadata.total;
    //             $("#page").text("Page " + Math.floor(offset/count+1) + " of " + (Math.ceil(total/count)));
    //             $("#resourceTable").find(".cloned").remove();
    //             data.content.forEach(function(item){
    //                 $( "#resourceRow" ).clone().prop("id",item.reviewId).appendTo( "#resourceTable" );
    //                 $("#"+item.reviewId).find("#showId").text(item.showId);
    //                 $("#"+item.reviewId).find("#episodeId").text(item.episodeId);
    //                 $("#"+item.reviewId).find("#rate").text(item.rate);
    //                 $("#"+item.reviewId).find("#topic").text(item.reviewTopic);
    //                 $("#"+item.reviewId).find("#content").text(item.reviewContent);
    //                 $("#"+item.reviewId).find("#likes").text(item.likes);
    //                 $("#"+item.reviewId).prop("class","cloned");
    //                 $("#"+item.reviewId).show();
    //             });
    //         })
    //         .fail(function(data){
    //             $("#reviewlist").text("Sorry no reviewss");
    //         })
    //
    // }

    $('#newUser').on('show.bs.modal', function (event) {
        // var button = $(event.relatedTarget) // Button that triggered the modal

        // var reviewId = button.parents("tr").attr("id");
        // var topic = button.parents("tr").find("#Topic").text();
        // var content = button.parents("tr").find("#Content").text();
        // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
        // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
        // var modal = $(this)
        // modal.find('.modal-title').text('Now you can edit this review');
        // // modal.find('.modal-body input').val(topic);
        // $('#topictext').val(topic);
        // $('#contenttext').val(content);
        // var editedtopic = modal.find('.modal-body input').text();
        // var editedcontent = modal.find('.modal-body textarea').text();
        // $("#registername").val("Input you name");

        $("#postnewuser").click(function (e) {
            var username = $("#registername").val();
            var email = $("#registeremail").val();
            var pwd = $("#registerpwd").val();
            var rptpwd = $("#repeatpwd").val();
            if(username == "" || email == "" || pwd == "" || rptpwd == ""){
                alert("Please fill all blanks.");
            } else if((pwd !== rptpwd)){
                alert("You should re-enter the same password you want to set.");
            } else {
                var obj = Object();
                obj.userName = $('#registername').val();
                obj.email = $('#registeremail').val();
                obj.password = $('#registerpwd').val();
                var patchinfo = JSON.stringify(obj);
                jQuery.ajax({
                    url: "/api/users",
                    type: "POST",
                    data: patchinfo,
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    // beforeSend: function (xhr) {
                    //     xhr.setRequestHeader("Authorization", token);
                    // }
                })
                    .done(function (data) {
                        alert("Welcome to Showtime!");
                        $("#closenew").trigger("click");
                        $("#inputEmail").val(data.content.email);
                        $("#inputPassword").val(data.content.password);
                    })
                    .fail(function (data) {
                        alert("Failed to register!");
                    })
            }
            // jQuery.ajax ({
            //
            //     url:  "/api/users",
            //     type: "POST",
            //     data: JSON.stringify({reviewTopic:$('#topictext').val(), reviewContent: $('#contenttext').val()}),
            //     dataType: "json",
            //     contentType: "application/json; charset=utf-8",
            //     beforeSend: function (xhr) {
            //         xhr.setRequestHeader ("Authorization", token);
            //     }
            // })
            //     .done(function(data){
            //         $("#closethis").trigger("click");
            //     })
            //     .fail(function(data){
            //         alert("Failed to edit!");
            //     })
        });
    });

})




