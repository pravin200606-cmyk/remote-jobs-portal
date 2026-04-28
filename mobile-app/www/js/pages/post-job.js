import API from '../api.js';

export function renderPostJob(container) {
  const user = window.appState.user;
  if (!user || user.role?.toLowerCase() !== 'employer') {
    container.innerHTML = `<div class="page empty-state"><div class="empty-icon">🔒</div><h3>Employers Only</h3><p>Login as an employer to post jobs.</p></div>`; return;
  }

  container.innerHTML = `<div class="page">
    <div class="form-card">
      <div class="form-title">Post a Job 💼</div>
      <div class="form-sub">Reach thousands of remote job seekers</div>
      <div class="form-group"><label>Job Title *</label><input id="pjTitle" type="text" placeholder="e.g. Senior React Developer"/></div>
      <div class="form-group"><label>Company Name *</label><input id="pjCompany" type="text" placeholder="Your company name"/></div>
      <div class="form-row">
        <div class="form-group"><label>Job Type</label>
          <select id="pjType">
            <option value="FULL_TIME">Full-time</option>
            <option value="PART_TIME">Part-time</option>
            <option value="CONTRACT">Contract</option>
            <option value="FREELANCE">Freelance</option>
          </select>
        </div>
        <div class="form-group"><label>Category</label>
          <select id="pjCategory">
            ${['Engineering','Design','Marketing','Data Science','Security','Mobile','DevOps','Finance'].map(c=>`<option value="${c}">${c}</option>`).join('')}
          </select>
        </div>
      </div>
      <div class="form-group"><label>Salary Range</label><input id="pjSalary" type="text" placeholder="e.g. $80,000 - $120,000/yr"/></div>
      <div class="form-group"><label>Location</label><input id="pjLocation" type="text" value="Remote" placeholder="Remote / Timezone"/></div>
      <div class="form-group"><label>Experience Required</label><input id="pjExp" type="text" placeholder="e.g. 3+ years"/></div>
      <div class="form-group"><label>Job Description *</label><textarea id="pjDesc" placeholder="Describe the role, responsibilities..."></textarea></div>
      <div class="form-group"><label>Requirements (one per line)</label><textarea id="pjReqs" placeholder="- 3+ years React&#10;- Node.js experience&#10;- Remote communication"></textarea></div>
      <div class="form-group"><label>Benefits</label><textarea id="pjBenefits" placeholder="Health insurance, flexible hours..."></textarea></div>
      <div class="form-group"><label>Application Deadline</label><input id="pjDeadline" type="date"/></div>
      <button class="btn btn-primary btn-block" id="postJobBtn" onclick="doPostJob()">🚀 Post Job</button>
    </div><br/>
  </div>`;

  window.doPostJob = async () => {
    const btn = document.getElementById('postJobBtn');
    const title = document.getElementById('pjTitle').value.trim();
    const description = document.getElementById('pjDesc').value.trim();
    if (!title || !description) { showToast('Title and description are required', 'error'); return; }
    btn.disabled = true; btn.textContent = 'Posting...';
    try {
      await API.postJob({
        title, description,
        company: document.getElementById('pjCompany').value,
        type: document.getElementById('pjType').value,
        category: document.getElementById('pjCategory').value,
        salary: document.getElementById('pjSalary').value,
        location: document.getElementById('pjLocation').value,
        experience: document.getElementById('pjExp').value,
        requirements: document.getElementById('pjReqs').value,
        benefits: document.getElementById('pjBenefits').value,
        deadline: document.getElementById('pjDeadline').value,
      });
      showToast('Job posted successfully! 🎉', 'success');
      navigate('dashboard');
    } catch (e) {
      showToast(e.message || 'Failed to post job', 'error');
      btn.disabled = false; btn.textContent = '🚀 Post Job';
    }
  };
}
