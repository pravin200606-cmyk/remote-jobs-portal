import API from '../api.js';
import { jobCard } from './home.js';

export async function renderDashboard(container) {
  const user = window.appState.user;
  if (!user) { container.innerHTML = `<div class="page empty-state"><div class="empty-icon">🔑</div><h3>Not logged in</h3><button class="btn btn-primary" onclick="navigate('login')">Login</button></div>`; return; }

  const role = user.role?.toLowerCase();

  if (role === 'employer') {
    let jobs = [];
    try { jobs = await API.myJobs(); } catch {}
    container.innerHTML = `<div class="page">
      <div class="profile-header">
        <div class="profile-avatar">🏢</div>
        <div class="profile-name">${user.name}</div>
        <span class="profile-role">EMPLOYER</span>
      </div>
      <div class="stats-row">
        <div class="stat-card"><div class="stat-num">${jobs.length}</div><div class="stat-label">Posted Jobs</div></div>
        <div class="stat-card"><div class="stat-num">${jobs.filter(j=>j.active).length}</div><div class="stat-label">Active</div></div>
        <div class="stat-card"><div class="stat-num">${jobs.reduce((s,j)=>s+(j.applicationCount||0),0)}</div><div class="stat-label">Applications</div></div>
      </div>
      <div class="section-header">
        <span class="section-title">My Job Listings</span>
        <button class="section-link" onclick="navigate('post-job')">+ Post New</button>
      </div>
      <div class="jobs-list">
        ${jobs.length ? jobs.map(j => `
          <div class="job-card">
            <div class="job-card-header">
              <div class="job-logo">💼</div>
              <div class="job-info">
                <div class="job-title">${j.title}</div>
                <div class="job-company">${j.applicationCount||0} applicants</div>
              </div>
            </div>
            <div class="job-tags"><span class="tag ${j.active?'green':''}">${j.active?'Active':'Closed'}</span><span class="tag yellow">${j.type||'Full-time'}</span></div>
            <div class="job-footer">
              <button class="btn btn-sm btn-outline" onclick="navigate('job-detail',{id:${j.id}})">View</button>
              <button class="btn btn-sm btn-danger" onclick="deleteJob(${j.id})">Delete</button>
            </div>
          </div>`).join('') : `<div class="empty-state"><div class="empty-icon">💼</div><h3>No jobs posted</h3><button class="btn btn-primary" onclick="navigate('post-job')">Post Your First Job</button></div>`}
      </div><br/>
    </div>`;

    window.deleteJob = async (id) => {
      if (!confirm('Delete this job?')) return;
      try { await API.deleteJob(id); showToast('Job deleted', 'success'); navigate('dashboard'); }
      catch (e) { showToast(e.message, 'error'); }
    };

  } else {
    let apps = [];
    try { apps = await API.myApplications(); } catch {}
    container.innerHTML = `<div class="page">
      <div class="profile-header">
        <div class="profile-avatar">👤</div>
        <div class="profile-name">${user.name}</div>
        <span class="profile-role">JOB SEEKER</span>
      </div>
      <div class="stats-row">
        <div class="stat-card"><div class="stat-num">${apps.length}</div><div class="stat-label">Applied</div></div>
        <div class="stat-card"><div class="stat-num">${apps.filter(a=>a.status==='ACCEPTED').length}</div><div class="stat-label">Accepted</div></div>
        <div class="stat-card"><div class="stat-num">${apps.filter(a=>a.status==='PENDING').length}</div><div class="stat-label">Pending</div></div>
      </div>
      <div class="section-header"><span class="section-title">Quick Actions</span></div>
      <div style="display:flex;flex-direction:column;gap:10px">
        <button class="btn btn-primary btn-block" onclick="navigate('jobs')">🔍 Browse Jobs</button>
        <button class="btn btn-outline btn-block" onclick="navigate('applications')">📋 View Applications</button>
        <button class="btn btn-outline btn-block" onclick="navigate('profile')">👤 Edit Profile</button>
      </div><br/>
    </div>`;
  }
}
