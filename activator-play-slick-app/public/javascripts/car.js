
var SUCCESS = 'success';
var ERROR = 'error';

var serverErrorMessage = 'Oops, something wrong :(';

$(document).ready(function() {
    $('#carDataTable').DataTable( {
        "ajax": {
            "url": "/car/list",
            "dataType": "json"
        },
         "columns": [
                    { "data": "name" },
                    { "data": "modal" },
                    { "data": "companyName" },
                    { data: "id" ,
                     "render": function ( data) {
                                  return '<i id=" ' + data +' " class="edit-button glyphicon glyphicon-edit cursorPointer" ></i>';
                                }
                            },
                     { data: "id" ,
                        "render": function ( data ) {
                                   return '<i id=" ' + data +' " class="remove-button glyphicon glyphicon-trash cursorPointer"></i>';
                               }
                     }

                ]
    } );

    var tableCar = $('#carDataTable').DataTable();

    // Delete car event
    $("body").on( 'click', '.remove-button', function () {
        var currentRow = $(this);
        var carId = $(this).attr('id').trim();
         bootbox.confirm("Are you sure?", function(result) {
            if(result) {
                    $.ajax({
                     url: "/car/delete",
                     type: "GET",
                     data: {carId: carId},
                     success:function(response){
                               if(response.status == SUCCESS) {
                                  showSuccessAlert(response.msg);
                                  newCar.row(currentRow.parents('tr') ).remove().draw();
                              } else {
                                  showErrorAlert(serverErrorMessage);
                              }
                        },
                     error: function(){
                          showErrorAlert(serverErrorMessage);
                       }
                  });
            } else {
               //
              }
         });
    });
     

     // Edit car event
     $("body").on( 'click', '.edit-button', function () {
            var carId = $(this).attr('id').trim();
             $.ajax({
                   url: "/car/edit",
                   type: "GET",
                   data: {carId: carId},
                   success:function(response){
                             $('#carEditModal').modal('show');
                             $.each(response.data, function(key, value){
                                $('#carEditModal input[name="'+key+'"]').val(value);
                             });
                      },
                   error: function(){
                             showErrorAlert(serverErrorMessage);
                     }
                });
          });


$('#carModal').on('shown.bs.modal', function () {
  $('#carForm').trigger("reset");
});

// Show success alert message
var showSuccessAlert = function (message) {
   	$.toaster({ priority : 'success', title : 'Success', message : message});
}

// Show error alert message
var showErrorAlert = function (message) {
    $.toaster({ priority : 'danger', title : 'Error', message : message});
}

// Convert form data in JSON format
$.fn.serializeObject = function() {
           var o = {};
           var a = this.serializeArray();
           $.each(a, function() {
                    if (o[this.name] !== undefined) {
                        if (!o[this.name].push) {
                            o[this.name] = [o[this.name]];
                        }
                        o[this.name].push(this.value || '');
                    } else {
                          if(this.name == 'id') {
                             o[this.name] = parseInt(this.value) || 0;
                          } else {
                         o[this.name] = this.value || '';
                         }
                    }
               });
            return JSON.stringify(o);
        };

// Handling form submission for create new car
      $('#carForm').on('submit', function(e){
         var formData = $("#carForm").serializeObject();
         var carTable = $('#carDataTable').dataTable();
          e.preventDefault();
           $.ajax({
                url: "/car/create",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                data: formData,
                success:function(response){
                   if(response.status == "success") {
                         $('#carModal').modal('hide');
                         var newCar = jQuery.parseJSON(formData);
                         newCar['id'] = response.data['id'];
                         carTable.fnAddData([newCar]);
                         showSuccessAlert(response.msg);
                   } else {
                        $('#carModal').modal('hide');
                        showErrorAlert(response.msg);
                   }
                },
                error: function(){
                    $('#carModal').modal('hide');
                    showErrorAlert(serverErrorMessage);
                }

            });
            return false;
      });

// Handling form submission for update car
$('#carEditForm').on('submit', function(e){
               var formData = $("#carEditForm").serializeObject();
                e.preventDefault();
                 $.ajax({
                      url: "/car/update",
                      type: "POST",
                      contentType: "application/json; charset=utf-8",
                      dataType: "json",
                      data: formData,
                      success:function(response){
                         if(response.status == SUCCESS) {
                               $('#carEditModal').modal('hide');
                               $('#carDataTable').DataTable().ajax.reload();
                               showSuccessAlert(response.msg)
                         } else {
                            $('#carEditModal').modal('hide');
                            showErrorAlert(response.msg);
                         }
                      },
                      error: function(){
                          $('#carEditModal').modal('hide');
                          showErrorAlert(serverErrorMessage);
                      }

                  });
                  return false;
            });

});