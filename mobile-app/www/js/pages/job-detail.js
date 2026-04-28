import API from '../api.js';

export async function renderJobDetail(container, id) {
  let job = null;
  try { job = await API.job(id); } catch (e) {
    container.innerHTML = `<div class="page empty-state"><div class="empty-icon">⚠️</div><h3>Job not found</h3><p>${e.message}</p></div>`; return;
  }
  const user = window.appState.user;
  const isSeeker = user?.role?.toLowerCase() === 'jobseeker';

  container.innerHTML = `<div class="page">
    <div class="detail-banner">
      <div class="detail-company-row">
        <div class="detail-logo">💼</div>
        <div>
          <div class="detail-company-name">${job.company || job.companyName || 'Company'}</div>
          <div class="detail-title">${job.title}</div>
        </div>
      </div>
      <div class="detail-meta">
        <span class="tag">${job.type || 'Full-time'}</span>
        <span class="tag green">${job.location || 'Remote'}</span>
        ${job.category ? `<span class="tag yellow">${job.category}</span>` : ''}
        ${job.salary ? `<span class="tag" style="color:var(--success)">${job.salary}</span>` : ''}
      </div>
    </div>
    <div class="detail-section">
      <h3>About the Role</h3>
      <p>${job.description || 'No description provided.'}</p>
    </div>
    ${job.requirements ? `<div class="detail-section"><h3>Requirements</h3><ul>${job.requirements.split('\n').filter(Boolean).map(r=>`<li>${r}</li>`).join('')}</ul></div>` : ''}
    ${job.benefits ? `<div class="detail-section"><h3>Benefits</h3><p>${job.benefits}</p></div>` : ''}
    <div class="detail-section">
      <h3>Job Details</h3>
      <p>📅 Posted: ${job.postedDate ? new Date(job.postedDate).toLocaleDateString() : 'Recently'}</p>
      ${job.deadline ? `<p>⏰ Deadline: ${new Date(job.deadline).toLocaleDateString()}</p>` : ''}
      ${job.experience ? `<p>💡 Experience: ${job.experience}</p>` : ''}
    </div>
    ${isSeeker ? `<div class="sticky-apply"><button class="btn btn-primary btn-block" id="applyBtn" onclick="applyNow(${job.id})">✅ Apply Now</button></div>` : ''}
    ${!user ? `<div class="sticky-apply"><button class="btn btn-outline btn-block" onclick="navigate('login')">🔑 Login to Apply</button></div>` : ''}
    <br/>
  </div>`;

  window.applyNow = async (jobId) => {
    const btn = document.getElementById('applyBtn');
    btn.disabled = true; btn.textContent = 'Applying...';
    try {
      await API.apply(jobId, { coverLetter: '' });
      showToast('Application submitted! 🎉', 'success');
      btn.textContent = '✅ Applied!';
    } catch (e) {
      showToast(e.message || 'Failed to apply', 'error');
      btn.disabled = false; btn.textContent = '✅ Apply Now';
    }
  };
}
