import API from '../api.js';

export async function renderApplications(container) {
  const user = window.appState.user;
  if (!user) { container.innerHTML = `<div class="page empty-state"><div class="empty-icon">🔑</div><h3>Not logged in</h3><p>Login to see your applications.</p><button class="btn btn-primary" onclick="navigate('login')">Login</button></div>`; return; }

  container.innerHTML = `<div class="page"><div class="loader"><div class="spinner"></div></div></div>`;

  let apps = [];
  try { apps = await API.myApplications(); } catch (e) {
    container.innerHTML = `<div class="page empty-state"><div class="empty-icon">⚠️</div><h3>Error</h3><p>${e.message}</p></div>`; return;
  }

  if (!apps.length) {
    container.innerHTML = `<div class="page empty-state"><div class="empty-icon">📋</div><h3>No Applications Yet</h3><p>Browse jobs and apply to get started!</p><button class="btn btn-primary" onclick="navigate('jobs')">Browse Jobs</button></div>`; return;
  }

  const statusBadge = s => {
    const m = { PENDING:'badge-pending', ACCEPTED:'badge-accepted', REJECTED:'badge-rejected', REVIEWING:'badge-active' };
    return `<span class="badge ${m[s]||'badge-pending'}">${s||'Pending'}</span>`;
  };

  container.innerHTML = `<div class="page">
    <div class="section-header"><span class="section-title">My Applications (${apps.length})</span></div>
    <div class="jobs-list">
      ${apps.map(a => `
        <div class="job-card" onclick="navigate('job-detail',{id:${a.jobId||a.job?.id}})">
          <div class="job-card-header">
            <div class="job-logo">📄</div>
            <div class="job-info">
              <div class="job-title">${a.jobTitle || a.job?.title || 'Job Title'}</div>
              <div class="job-company">${a.company || a.job?.company || 'Company'}</div>
            </div>
          </div>
          <div class="job-footer">
            ${statusBadge(a.status)}
            <span class="job-date">${a.appliedDate ? new Date(a.appliedDate).toLocaleDateString() : 'Recently'}</span>
          </div>
        </div>`).join('')}
    </div>
  </div>`;
}
