import API from '../api.js';
import { jobCard } from './home.js';

let currentParams = {};

export async function renderJobs(container, params = {}) {
  currentParams = { ...params };

  container.innerHTML = `<div class="page">
    <div class="form-card" style="margin-bottom:16px">
      <div class="form-group" style="margin-bottom:10px">
        <input id="searchInput" type="text" placeholder="🔍 Search jobs..." value="${params.search||''}" />
      </div>
      <div class="form-row">
        <div class="form-group" style="margin-bottom:0">
          <select id="typeFilter">
            <option value="">All Types</option>
            <option ${params.type==='FULL_TIME'?'selected':''} value="FULL_TIME">Full-time</option>
            <option ${params.type==='PART_TIME'?'selected':''} value="PART_TIME">Part-time</option>
            <option ${params.type==='CONTRACT'?'selected':''} value="CONTRACT">Contract</option>
            <option ${params.type==='FREELANCE'?'selected':''} value="FREELANCE">Freelance</option>
          </select>
        </div>
        <div class="form-group" style="margin-bottom:0">
          <select id="categoryFilter">
            <option value="">All Categories</option>
            ${['Engineering','Design','Marketing','Data Science','Security','Mobile','DevOps','Finance'].map(c=>`<option ${params.category===c?'selected':''} value="${c}">${c}</option>`).join('')}
          </select>
        </div>
      </div>
      <button class="btn btn-primary btn-block" style="margin-top:10px" onclick="applyFilters()">Apply Filters</button>
    </div>
    <div id="jobResults"><div class="loader"><div class="spinner"></div></div></div>
  </div>`;

  window.applyFilters = () => {
    const search = document.getElementById('searchInput').value.trim();
    const type = document.getElementById('typeFilter').value;
    const category = document.getElementById('categoryFilter').value;
    loadJobs({ search, type, category });
  };

  document.getElementById('searchInput').addEventListener('keydown', e => {
    if (e.key === 'Enter') window.applyFilters();
  });

  await loadJobs(currentParams);
}

async function loadJobs(params) {
  const res = document.getElementById('jobResults');
  if (!res) return;
  res.innerHTML = `<div class="loader"><div class="spinner"></div></div>`;
  try {
    const data = await API.jobs(params);
    const jobs = data.content || data || [];
    if (!jobs.length) {
      res.innerHTML = `<div class="empty-state"><div class="empty-icon">🔍</div><h3>No jobs found</h3><p>Try different filters or search terms.</p></div>`;
      return;
    }
    res.innerHTML = `<div class="jobs-list">${jobs.map(jobCard).join('')}</div><p style="text-align:center;color:var(--text2);font-size:12px;padding:12px">Showing ${jobs.length} results</p>`;
  } catch (e) {
    res.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠️</div><h3>Failed to load</h3><p>${e.message}</p><button class="btn btn-primary" onclick="applyFilters()">Retry</button></div>`;
  }
}
