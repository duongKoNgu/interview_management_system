const goToOffer = document.getElementById('offerNe');
goToOffer.addEventListener('click', function() {
    let username = localStorage.getItem('username');
    let password = localStorage.getItem('password');
    
    let data = {
        username: username,
        password: password
    };
    
    let encodedData = encodeURIComponent(JSON.stringify(data));
    
    window.location.href = `http://127.0.0.1:5500/templates/offerIntermediate.html?data=${encodedData}`;

});