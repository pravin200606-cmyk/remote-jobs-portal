// API base — Android emulator uses 10.0.2.2, real device needs your LAN IP
const BASE = window.location.hostname === 'localhost' || window.location.hostname === ''
  ? 'http://10.241.139.83:8080'
  : 'http://10.241.139.83:8080';

const API = {
  BASE,

  // Auth
  login:       (d)  => post('/api/auth/login', d),
  register:    (d)  => post('/api/auth/register', d),
  logout:      ()   => post('/api/auth/logout', {}),
  me:          ()   => get('/api/auth/me'),

  // Jobs
  jobs:        (p)  => get('/api/jobs', p),
  job:         (id) => get(`/api/jobs/${id}`),
  apply:       (id, d) => post(`/api/jobs/${id}/apply`, d),

  // Job Seeker
  myApplications: () => get('/api/jobseeker/applications'),
  myProfile:   ()  => get('/api/jobseeker/profile'),
  updateProfile: (d) => put('/api/jobseeker/profile', d),

  // Employer
  myJobs:      ()  => get('/api/employer/jobs'),
  postJob:     (d) => post('/api/employer/jobs', d),
  updateJob:   (id,d) => put(`/api/employer/jobs/${id}`, d),
  deleteJob:   (id)   => del(`/api/employer/jobs/${id}`),
  jobApplicants: (id) => get(`/api/employer/jobs/${id}/applicants`),

  // Admin
  adminStats:  ()  => get('/api/admin/stats'),
  adminUsers:  ()  => get('/api/admin/users'),
  adminJobs:   ()  => get('/api/admin/jobs'),
};

async function get(path, params = {}) {
  const url = new URL(BASE + path);
  Object.entries(params).forEach(([k, v]) => v != null && url.searchParams.set(k, v));
  const res = await fetch(url, { credentials: 'include', headers: authHeaders() });
  return handleRes(res);
}

async function post(path, data) {
  const res = await fetch(BASE + path, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify(data),
  });
  return handleRes(res);
}

async function put(path, data) {
  const res = await fetch(BASE + path, {
    method: 'PUT',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify(data),
  });
  return handleRes(res);
}

async function del(path) {
  const res = await fetch(BASE + path, {
    method: 'DELETE',
    credentials: 'include',
    headers: authHeaders(),
  });
  return handleRes(res);
}

function authHeaders() {
  const token = localStorage.getItem('token');
  return token ? { 'Authorization': `Bearer ${token}` } : {};
}

async function handleRes(res) {
  if (res.status === 204) return {};
  const ct = res.headers.get('content-type') || '';
  const data = ct.includes('json') ? await res.json() : await res.text();
  if (!res.ok) throw new Error(data?.message || data || `HTTP ${res.status}`);
  return data;
}

export default API;
