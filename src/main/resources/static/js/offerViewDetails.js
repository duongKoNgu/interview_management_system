let Id = localStorage.getItem('myVariable');
console.log(Id);

//set userLogin
const userName = document.getElementById('username');
const userRole = document.getElementById('userrole')

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

})
.catch((err) => {
console.log(err);
});

const acceptedButton = document.getElementById('acceptedButton');
acceptedButton.addEventListener('click', () => {
  window.location.href = 'http://127.0.0.1:5500/templates/offerApproveReject.html';
});

const create = document.getElementById('date-create');
const updateBy = document.getElementById('byEdit');
const update = document.getElementById('date-edit');
const candidateName = document.getElementById('candidate-name');
const position = document.getElementById('position');
const Approver = document.getElementById('Approver');
const interviewInfo = document.getElementById('interview-info');
const interviewer = document.getElementById('interviewer');
const contractPeriodStart = document.getElementById('contract-period-start');
const contractPeriodEnd = document.getElementById('contract-period-end');
const interviewNotes = document.getElementById('interview-notes');
const contractType = document.getElementById('contract-type');
const level = document.getElementById('Level');
const department = document.getElementById('department');
const recruiterOwner = document.getElementById('recruiter-owner');
const dueDate = document.getElementById('due-date');
const basicSalary = document.getElementById('basic-salary');
const notes = document.getElementById('note');
const status = document.getElementById('status');

const params = new URLSearchParams();
params.append('id',Id);

axios.get('http://localhost:8088/offer/view', {
  params: params,
  auth: {
    username: username,
    password: password
  }
})
  .then(function(response) {

    
    console.log(response);
    data = response.data;

    candidateName.innerHTML = data.candidate;
    position.innerHTML = data.position;
    Approver.innerHTML = data.approver;
    interviewInfo.innerHTML = data.interviewInfo;
    interviewer.innerHTML = data.interviewer;
    contractPeriodStart.innerHTML = data.contractFrom;
    contractPeriodEnd.innerHTML = data.contractTo;
    interviewNotes.innerHTML = data.interviewNotes;
    contractType.innerHTML = data.contractType;
    level.innerHTML = data.level;
    department.innerHTML = data.department;
    recruiterOwner.innerHTML = data.recruiter;
    dueDate.innerHTML = data.dueDate;
    basicSalary.innerHTML = data.salaryBasic;
    notes.innerHTML = data.notes;
    status.innerHTML = data.status;
    create.innerHTML = data.createDate;
    update.innerHTML = data.editDate;
    updateBy.innerHTML = data.editLastBy;

  })
  .catch((err) => {
    console.log(err);
  });

  const edit = document.getElementById('edit');

edit.addEventListener('click', () => {

  window.location.href = 'http://127.0.0.1:5500/templates/offerEdit.html';
});

// cancel
const cancelButton = document.getElementById('cancelButton');
const confirmationModal = document.getElementById('confirmationModal');
const yesButton = document.getElementById('yesButton');
const noButton = document.getElementById('noButton');

cancelButton.addEventListener('click', () => {
  confirmationModal.style.display = 'block';
});

yesButton.addEventListener('click', () => {
  confirmationModal.style.display = 'none';
});

noButton.addEventListener('click', () => {
  confirmationModal.style.display = 'none';
});

window.addEventListener('click', (event) => {
  if (event.target === confirmationModal) {
    confirmationModal.style.display = 'none';
  }
});