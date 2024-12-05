

const userName = document.getElementById('username');
const userRole = document.getElementById('userrole')

if(userRole ==="Interviewer"){
  window.location.href = 'http://localhost:8088/homepage';
}

const username = localStorage.getItem('username');
const password = localStorage.getItem('password');

axios.get("http://localhost:8088/offer/userLogin", {
  params: {
    username: username,
    password: password
  },
  auth: {
    username: username,
    password: password
  }
}).then((res) => {
  
console.log(res);
userName.innerHTML = res.data.username;
userRole.innerHTML = res.data.role;

if(res.data.role ==="Interviewer"){
  window.location.href = 'http://localhost:8088/homepage';
}

})
.catch((err) => {
console.log(err);
});

//tải excel
document.getElementById('exportButton').onclick = () => document.getElementById('confirmationModal').style.display = 'block';

document.getElementById('yesButton').onclick = () => {
  const from = document.getElementById('contract-period-start').value;
  const to = document.getElementById('contract-period-end').value;
  
  if (!from || !to) {
    alert('Vui lòng nhập ngày hợp lệ.');
    return;
  }
  
  fetch(`http://localhost:8088/offer/exportExcel?from=${from}&to=${to}`, {
    headers: { 'Authorization': 'Basic ' + btoa('username:password') }
  })
    .then(res => res.blob())
    .then(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'Offer_list.xlsx';
      a.click();
      URL.revokeObjectURL(url);
    })
    .catch(err => alert('Lỗi: ' + err))
    .finally(() => document.getElementById('confirmationModal').style.display = 'none');
};

document.getElementById('noButton').onclick = () => document.getElementById('confirmationModal').style.display = 'none';

noButton.addEventListener('click', () => {
  confirmationModal.style.display = 'none';
});

window.addEventListener('click', (event) => {
  if (event.target == confirmationModal) {
    confirmationModal.style.display = 'none';
  }
});

const pageSize = 5;
let currentPage = 0;

function addRow(value0, value1, value2, value3, value4, value5, value6) {

  const tableBody = document.getElementById('dataTable').getElementsByTagName('tbody')[0];
  const newRow = document.createElement('tr');
  newRow.status = value6;
  newRow.id = value0;
  newRow.innerHTML = `
    <td>${value1}</td>
    <td>${value2}</td>
    <td>${value3}</td>
    <td>${value4}</td>
    <td>${value5}</td>
    <td>${value6}</td>
    <td>
      <span class="material-icons" id="view">visibility</span>
      <span class="material-icons" id="edit">edit</span>
    </td>
  `;

  newRow.querySelector('#view').addEventListener('click', function() {
    const trId = this.closest('tr').id;
    localStorage.setItem('myVariable', trId);

    const thisStatus = this.closest('tr').status; 
    if(thisStatus === "Waiting For Approval"){
       window.location.href = 'http://127.0.0.1:5500/templates/offerViewDetails.html';
    }
    if(thisStatus === "Approved"){
       window.location.href = 'http://127.0.0.1:5500/templates/offerSentToCandidate.html';
    }
    if(thisStatus === "Waiting For Response"){
       window.location.href = 'http://127.0.0.1:5500/templates/offerCandidate.html';
    }
  });

  newRow.querySelector('#edit').addEventListener('click', function() {
    const trId = this.closest('tr').id;
    localStorage.setItem('myVariable', trId);
    const thisStatus = this.closest('tr').status; 

    if(thisStatus === "Waiting For Approval"){
        window.location.href = 'http://127.0.0.1:5500/templates/offerEdit.html';
    }
  });

  tableBody.appendChild(newRow);
}

function changePage(data, newPage) {
  const totalPages = Math.ceil(data.length / pageSize);
  if (newPage >= 0 && newPage < totalPages) {
    currentPage = newPage;
    displayData(data, currentPage);
    updatePaginationButtons(totalPages);
  }
}

function displayData(data, pageIndex) {
  const tableBody = document.getElementById('dataTable').getElementsByTagName('tbody')[0];
  tableBody.innerHTML = '';

  const startIndex = pageIndex * pageSize;
  const endIndex = Math.min(startIndex + pageSize, data.length);
  const currentPageData = data.slice(startIndex, endIndex);

  for (let dt of currentPageData) {
    addRow(dt.id, dt.candidateName, dt.email, dt.approver, dt.department, dt.notes, dt.status);
  }
}

function updatePaginationButtons(totalPages) {
  const buttons = document.getElementById('pagination-container').getElementsByTagName('button');
  
  buttons[0].disabled = currentPage === 0; 
  buttons[1].disabled = currentPage === 0; 
  buttons[buttons.length - 2].disabled = currentPage === totalPages - 1; 
  buttons[buttons.length - 1].disabled = currentPage === totalPages - 1;

  for (let i = 2; i < buttons.length - 2; i++) {
    buttons[i].classList.toggle('active', i - 2 === currentPage);
  }
}

function changePage(data, newPage) {
  const totalPages = Math.ceil(data.length / pageSize);
  if (newPage >= 0 && newPage < totalPages) {
    currentPage = newPage;
    render(data); // Gọi lại hàm render để cập nhật toàn bộ phân trang
  }
}

function render(data) {
  const paginationContainer = document.getElementById('pagination-container');
  paginationContainer.innerHTML = '';
  
  const totalPages = Math.ceil(data.length / pageSize);
  // Nút First
  const firstButton = document.createElement('button');
  firstButton.textContent = 'First';
  firstButton.addEventListener('click', () => changePage(data, 0));
  firstButton.disabled = currentPage === 0;
  paginationContainer.appendChild(firstButton);
  
  // Nút Previous
  const prevButton = document.createElement('button');
  prevButton.textContent = 'Previous';
  prevButton.addEventListener('click', () => changePage(data, currentPage - 1));
  prevButton.disabled = currentPage === 0;
  paginationContainer.appendChild(prevButton);
  
  // Hiển thị các nút trang
  const visiblePages = 2; // Số trang hiển thị ở mỗi bên của trang hiện tại
  let startPage = Math.max(0, currentPage - visiblePages);
  let endPage = Math.min(totalPages - 1, currentPage + visiblePages);

  // Thêm dấu "..." ở đầu nếu cần
  if (startPage > 0) {
    const ellipsisStart = document.createElement('span');
    ellipsisStart.textContent = '...';
    paginationContainer.appendChild(ellipsisStart);
  }

  for (let i = startPage; i <= endPage; i++) {
    const pageButton = document.createElement('button');
    pageButton.textContent = i + 1;
    pageButton.addEventListener('click', () => changePage(data, i));
    if (i === currentPage) {
      pageButton.classList.add('active');
    }
    paginationContainer.appendChild(pageButton);
  }

  // Thêm dấu "..." ở cuối nếu cần
  if (endPage < totalPages - 1) {
    const ellipsisEnd = document.createElement('span');
    ellipsisEnd.textContent = '...';
    paginationContainer.appendChild(ellipsisEnd);
  }
  
  // Nút Next
  const nextButton = document.createElement('button');
  nextButton.textContent = 'Next';
  nextButton.addEventListener('click', () => changePage(data, currentPage + 1));
  nextButton.disabled = currentPage === totalPages - 1;
  paginationContainer.appendChild(nextButton);
  
  // Nút Last
  const lastButton = document.createElement('button');
  lastButton.textContent = 'Last';
  lastButton.addEventListener('click', () => changePage(data, totalPages - 1));
  lastButton.disabled = currentPage === totalPages - 1;
  paginationContainer.appendChild(lastButton);
  
  displayData(data, currentPage);
}


 //searching 
function getDepartment() {
var selectElement = document.getElementById('department');
var selectedValue = selectElement.value;
}
 
const searchInput = document.getElementById('search-input');
const buttonSearch = document.getElementById('search-button');

buttonSearch.addEventListener('click', function() {
  
  getBySearch();
});

function getBySearch(){

  const inputValue = [];
  inputValue.push(searchInput.value);
  var selectElement = document.getElementById('department');
  var selectElementStatus = document.getElementById('status');
  inputValue.push(selectElement.value);
  inputValue.push(selectElementStatus.value);

  console.log(inputValue);

  axios.post('http://localhost:8088/offer/postSearch', {
    data: inputValue
  },{auth: {
    username: localStorage.getItem('username'),
    password: localStorage.getItem('password')
  }})
  .then(function (response) {
    render(response.data);
    console.log(response);
    
  })
  .catch(function (error) {
    console.log(error);
    window.location.href = 'http://localhost:8088/homepage';
    
  });
  
}

//gợi ý searching 

const suggestionsContainer = document.getElementById('suggestions');

const items = [];

searchInput.addEventListener('input', function() {
    const searchTerm = this.value.toLowerCase();
    const filteredItems = items.filter(item => 
        item.toLowerCase().includes(searchTerm)
    );

    displaySuggestions(filteredItems);
});

function displaySuggestions(suggestions) {
    suggestionsContainer.innerHTML = '';
    suggestions.forEach(item => {
        const div = document.createElement('div');
        div.textContent = item;
        div.addEventListener('click', function() {
            searchInput.value = this.textContent;
            hideSuggestions();
        });
        suggestionsContainer.appendChild(div);
    });
}

// Thêm hàm mới để ẩn gợi ý
function hideSuggestions() {
    suggestionsContainer.innerHTML = '';
}

// Thêm sự kiện click cho document
document.addEventListener('click', function(event) {
    if (!searchInput.contains(event.target) && !suggestionsContainer.contains(event.target)) {
        hideSuggestions();
    }
});

// Ngăn chặn sự kiện click trên ô input lan ra document
searchInput.addEventListener('click', function(event) {
    event.stopPropagation();
});



axios
.get("http://localhost:8088/offer/getAllOffers", {auth: {
    username: localStorage.getItem('username'),
    password: localStorage.getItem('password')
  }})
.then((res) => {
 for(var i = 0; i < res.data.length; i++){
  items.push(res.data[i].candidateName);
 } 
render(res.data);
  
console.log(res);
})
.catch((err) => {
console.log(err);
});