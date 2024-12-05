document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.edit-button').addEventListener('click', function () {
        const email = document.querySelector('input[name="email"]').value.trim();
        window.location.href = '/user/userUpdate/' + email;
    });
});

function showDialog() {
    document.getElementById('confirmDialog').style.display = 'flex';
}

function closeDialog() {
    document.getElementById('confirmDialog').style.display = 'none';
}

document.querySelector('.btn-yes').addEventListener('click', function () {
    window.location.href = '/login';
});

