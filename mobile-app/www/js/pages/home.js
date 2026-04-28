import API from '../api.js';

export async function renderHome(container) {
  let jobs = [];
  let stats = { totalJobs: '2.4k+', companies: '340+', placements: '1.8k+' };
  try { const r = await API.jobs({ size: 4 }); jobs = r.content || r || []; } catch {}

  container.innerHTML = `<div class="page">
    <div class="hero">
      <h1>🌐 Find Remote Jobs</h1>
      <p>Work from anywhere. Discover thousands of remote positions across the globe.</p>
      <div class="hero-search">
        <input id="heroSearch" type="text" placeholder="Search jobs, companies..." />
        <button class="btn btn-primary" onclick="doHeroSearch()">🔍</button>
      </div>
    </div>
    <div class="stats-row">
      <div class="stat-card"><div class="stat-num">${stats.totalJobs}</div><div class="stat-label">Jobs Listed</div></div>
      <div class="stat-card"><div class="stat-num">${stats.companies}</div><div class="stat-label">Companies</div></div>
      <div class="stat-card"><div class="stat-num">${stats.placements}</div><div class="stat-label">Placements</div></div>
    </div>
    <div class="section-header">
      <span class="section-title">Latest Jobs</span>
      <button class="section-link" onclick="navigate('jobs')">View all →</button>
    </div>
    <div class="jobs-list">${jobs.length ? jobs.map(jobCard).join('') : '<div class="empty-state"><div class="empty-icon">💼</div><h3>No jobs yet</h3><p>Check back soon!</p></div>'}</div>
    <br/>
    <div class="section-header"><span class="section-title">Top Categories</span></div>
    <div class="stats-row">
      ${[['💻','Engineering'],['🎨','Design'],['📊','Marketing'],['🧪','Data Science'],['🔐','Security'],['📱','Mobile']].map(([i,l])=>`<div class="stat-card" onclick="navigate('jobs',{category:'${l}'})" style="cursor:pointer"><div style="font-size:24px">${i}</div><div class="stat-label">${l}</div></div>`).join('')}
    </div>
  </div>`;

  window.doHeroSearch = () => {
    const q = document.getElementById('heroSearch').value.trim();
    if (q) navigate('jobs', { search: q });
  };
  document.getElementById('heroSearch').addEventListener('keydown', e => {
    if (e.key === 'Enter') window.doHeroSearch();
  });
}

export function jobCard(j) {
  const emoji = ['💻','🎨','📊','🧪','🔐','📱','☁️','🖥️'][Math.floor(Math.random()*8)];
  return `<div class="job-card" onclick="navigate('job-detail',{id:${j.id}})">
    <div class="job-card-header">
      <div class="job-logo">${emoji}</div>
      <div class="job-info">
        <div class="job-title">${j.title || 'Job Title'}</div>
        <div class="job-company">${j.company || j.companyName || 'Company'}</div>
      </div>
    </div>
    <div class="job-tags">
      <span class="tag">${j.type || 'Full-time'}</span>
      <span class="tag green">${j.location || 'Remote'}</span>
      ${j.category ? `<span class="tag yellow">${j.category}</span>` : ''}
    </div>
    <div class="job-footer">
      <span class="job-salary">${j.salary || j.salaryRange || '💰 Competitive'}</span>
      <span class="job-date">${j.postedDate ? new Date(j.postedDate).toLocaleDateString() : 'Recently'}</span>
    </div>
  </div>`;
}
