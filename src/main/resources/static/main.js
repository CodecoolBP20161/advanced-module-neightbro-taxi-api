/**
 * Created by david on 3/13/17.
 */
$(function() {
    $('a[href="#"]').on('click', function (e) {
        e.preventDefault();
        return false;
    })
});

