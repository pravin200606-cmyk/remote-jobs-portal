import API from './api.js';
import { renderHome } from './pages/home.js';
import { renderJobs } from './pages/jobs.js';
import { renderJobDetail } from './pages/job-detail.js';
import { renderLogin } from './pages/login.js';
import { renderRegister } from './pages/register.js';
import { renderProfile } from './pages/profile.js';
import { renderApplications } from './pages/applications.js';
import { renderPostJob } from './pages/post-job.js';
import { renderDashboard } from './pages/dashboard.js';
import { renderAdmin } from './pages/admin.js';

// ── State ──
window.appState = {
  user: null,
  history: [],
};

// ── Toast ──
window.showToast = function(msg, type = 'info') {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.className = `toast ${type}`;
  t.classList.remove('hidden');
  setTimeout(() => t.classList.add('hidden'), 3000);
};

// ── Loader ──
window.showLoader = function() {
  document.getElementById('pageContainer').innerHTML = `<div class="loader"><div class="spinner"></div></div>`;
};

// ── Router ──
window.router = {
  history: [],
  back() {
    if (this.history.length > 1) {
      this.history.pop();
      const prev = this.history[this.history.length - 1];
      navigate(prev.page, prev.params, true);
    }
  }
};

window.navigate = async function(page, params = {}, isBack = false) {
  if (!isBack) router.history.push({ page, params });
  const container = document.getElementById('pageContainer');
  const backBtn = document.getElementById('backBtn');
  const title = document.getElementById('topbarTitle');

  // Bottom nav active
  document.querySelectorAll('.bnav-item').forEach(b => {
    b.classList.toggle('active', b.dataset.page === page);
  });

  // Back button logic
  backBtn.classList.toggle('hidden', router.history.length <= 1);

  showLoader();

  try {
    switch (page) {
      case 'home':        title.textContent='RemoteWork Hub'; await renderHome(container); break;
      case 'jobs':        title.textContent='Browse Jobs';    await renderJobs(container, params); break;
      case 'job-detail':  title.textContent='Job Details';    await renderJobDetail(container, params.id); break;
      case 'login':       title.textContent='Sign In';        renderLogin(container); break;
      case 'register':    title.textContent='Create Account'; renderRegister(container); break;
      case 'profile':     title.textContent='My Profile';     await renderProfile(container); break;
      case 'applications':title.textContent='Applications';   await renderApplications(container); break;
      case 'post-job':    title.textContent='Post a Job';     renderPostJob(container); break;
      case 'dashboard':   title.textContent='Dashboard';      await renderDashboard(container); break;
      case 'admin':       title.textContent='Admin Panel';    await renderAdmin(container); break;
      default:            title.textContent='RemoteWork Hub'; await renderHome(container);
    }
  } catch (e) {
    container.innerHTML = `<div class="page empty-state"><div class="empty-icon">⚠️</div><h3>Error</h3><p>${e.message}</p><button class="btn btn-primary" onclick="navigate('home')">Go Home</button></div>`;
  }
};

// ── Drawer ──
window.toggleMenu = function() {
  document.getElementById('drawer').classList.toggle('open');
  document.getElementById('drawerOverlay').classList.toggle('hidden');
};
window.closeMenu = function() {
  document.getElementById('drawer').classList.remove('open');
  document.getElementById('drawerOverlay').classList.add('hidden');
};

// ── Logout ──
window.logout = async function() {
  try { await API.logout(); } catch {}
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.appState.user = null;
  updateNavForUser(null);
  closeMenu();
  navigate('home');
  showToast('Logged out successfully', 'success');
};

// ── Update nav based on user role ──
window.updateNavForUser = function(user) {
  const loggedIn = !!user;
  const role = user?.role?.toLowerCase() || '';

  document.getElementById('navLogin').classList.toggle('hidden', loggedIn);
  document.getElementById('navRegister').classList.toggle('hidden', loggedIn);
  document.getElementById('navLogout').classList.toggle('hidden', !loggedIn);
  document.getElementById('navDashboard').classList.toggle('hidden', !loggedIn);
  document.getElementById('navProfile').classList.toggle('hidden', !loggedIn);
  document.getElementById('navApplications').classList.toggle('hidden', role !== 'jobseeker');
  document.getElementById('navPostJob').classList.toggle('hidden', role !== 'employer');
  document.getElementById('navAdmin').classList.toggle('hidden', role !== 'admin');

  // drawer header
  document.getElementById('drawerName').textContent = user?.name || 'Guest';
  document.getElementById('drawerRole').textContent = user ? role : 'Not logged in';

  // bottom nav
  document.getElementById('bnavApplications').classList.toggle('hidden', role !== 'jobseeker');
  document.getElementById('bnavProfile').classList.toggle('hidden', !loggedIn);
};

// ── Network status ──
function handleNetwork() {
  const online = navigator.onLine;
  const existing = document.getElementById('offlineBanner');
  if (!online && !existing) {
    const b = document.createElement('div');
    b.id = 'offlineBanner';
    b.className = 'offline-banner';
    b.innerHTML = '⚠️ You are offline. Some features may not work.';
    document.getElementById('pageContainer').prepend(b);
  } else if (online && existing) {
    existing.remove();
  }
}
window.addEventListener('online', handleNetwork);
window.addEventListener('offline', handleNetwork);

// ── Init ──
async function init() {
  // Try to restore session
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    try {
      window.appState.user = JSON.parse(savedUser);
      updateNavForUser(window.appState.user);
      
      // Verify session is still valid in background
      API.me().then(me => {
        window.appState.user = me;
        localStorage.setItem('user', JSON.stringify(me));
        updateNavForUser(window.appState.user);
      }).catch(() => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        window.appState.user = null;
        updateNavForUser(null);
      });
    } catch {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      window.appState.user = null;
      updateNavForUser(null);
    }
  } else {
    updateNavForUser(null);
  }

  // Hide splash, show app
  setTimeout(() => {
    const splash = document.getElementById('splash');
    splash.classList.add('fade-out');
    setTimeout(() => {
      splash.classList.add('hidden');
      document.getElementById('app').classList.remove('hidden');
      navigate('home');
    }, 500);
  }, 1500);
}

init();
