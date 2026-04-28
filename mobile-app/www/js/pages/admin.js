import API from '../api.js';

export async function renderAdmin(container) {
  const user = window.appState.user;
  if (!user || user.role?.toLowerCase() !== 'admin') {
    container.innerHTML = `<div class="page empty-state"><div class="empty-icon">🔒</div><h3>Admin Only</h3><p>You don't have permission to access this page.</p></div>`; return;
  }

  let stats = {}, users = [], jobs = [];
  try { stats = await API.adminStats(); } catch {}
  try { users = await API.adminUsers(); } catch {}
  try { jobs = await API.adminJobs(); } catch {}

  container.innerHTML = `<div class="page">
    <div class="profile-header">
      <div class="profile-avatar">🔧</div>
      <div class="profile-name">Admin Panel</div>
      <span class="profile-role">ADMINISTRATOR</span>
    </div>
    <div class="admin-grid">
      <div class="admin-card"><div class="admin-icon">👥</div><div class="admin-num">${stats.totalUsers||users.length||0}</div><div class="admin-label">Total Users</div></div>
      <div class="admin-card"><div class="admin-icon">💼</div><div class="admin-num">${stats.totalJobs||jobs.length||0}</div><div class="admin-label">Total Jobs</div></div>
      <div class="admin-card"><div class="admin-icon">📋</div><div class="admin-num">${stats.totalApplications||0}</div><div class="admin-label">Applications</div></div>
      <div class="admin-card"><div class="admin-icon">🏢</div><div class="admin-num">${stats.totalEmployers||0}</div><div class="admin-label">Employers</div></div>
    </div>
    <div class="tabs">
      <button class="tab active" id="tabUsers" onclick="switchAdminTab('users')">Users</button>
      <button class="tab" id="tabJobs" onclick="switchAdminTab('jobs')">Jobs</button>
    </div>
    <div id="adminContent">
      ${renderUsersList(users)}
    </div>
  </div>`;

  window._adminUsers = users;
  window._adminJobs = jobs;

  window.switchAdminTab = (tab) => {
    document.getElementById('tabUsers').classList.toggle('active', tab==='users');
    document.getElementById('tabJobs').classList.toggle('active', tab==='jobs');
    document.getElementById('adminContent').innerHTML = tab==='users' ? renderUsersList(window._adminUsers) : renderJobsList(window._adminJobs);
  };
}

function renderUsersList(users) {
  if (!users.length) return `<div class="empty-state"><div class="empty-icon">👥</div><h3>No users</h3></div>`;
  return `<div class="jobs-list">${users.map(u=>`
    <div class="job-card">
      <div class="job-card-header">
        <div class="job-logo">👤</div>
        <div class="job-info">
          <div class="job-title">${u.name||u.email}</div>
          <div class="job-company">${u.email}</div>
        </div>
      </div>
      <div class="job-tags">
        <span class="tag ${u.role==='ADMIN'?'yellow':u.role==='EMPLOYER'?'green':''}">${u.role||'USER'}</span>
        <span class="tag ${u.active!==false?'green':''}">${u.active!==false?'Active':'Inactive'}</span>
      </div>
    </div>`).join('')}</div>`;
}

function renderJobsList(jobs) {
  if (!jobs.length) return `<div class="empty-state"><div class="empty-icon">💼</div><h3>No jobs</h3></div>`;
  return `<div class="jobs-list">${jobs.map(j=>`
    <div class="job-card">
      <div class="job-card-header">
        <div class="job-logo">💼</div>
        <div class="job-info">
          <div class="job-title">${j.title}</div>
          <div class="job-company">${j.company||j.companyName||''}</div>
        </div>
      </div>
      <div class="job-tags">
        <span class="tag ${j.active?'green':''}">${j.active?'Active':'Closed'}</span>
        <span class="tag yellow">${j.type||'Full-time'}</span>
      </div>
    </div>`).join('')}</div>`;
}
