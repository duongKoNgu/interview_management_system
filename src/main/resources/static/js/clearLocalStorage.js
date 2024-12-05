window.onload = function() {
    function resetLocalStorage() {
    localStorage.clear();
    console.log('localStorage đã được reset');
}

setInterval(resetLocalStorage, 300000);

function checkCredentials() {
  const username = localStorage.getItem('username');
  const password = localStorage.getItem('password');

  if (username && password) {
    console.log('Tìm thấy thông tin đăng nhập trong localStorage');
    return true;
  } else {
    alert('Hết phiên đăng nhập. Vui lòng đăng nhập lại.');
    window.location.href = 'http://localhost:8088/login';
    return false;
  }
}
checkCredentials();

};