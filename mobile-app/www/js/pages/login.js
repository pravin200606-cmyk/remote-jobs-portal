import API from '../api.js';

export function renderLogin(container) {
  container.innerHTML = `<div class="page">
    <div class="form-card">
      <div class="form-title">Welcome Back 👋</div>
      <div class="form-sub">Sign in to your RemoteWork Hub account</div>
      <div class="form-group">
        <label>Email Address</label>
        <input id="loginEmail" type="email" placeholder="you@example.com" autocomplete="email"/>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input id="loginPassword" type="password" placeholder="••••••••" autocomplete="current-password"/>
      </div>
      <button class="btn btn-primary btn-block" id="loginBtn" onclick="doLogin()">Sign In</button>
      <div class="form-link">
        Don't have an account? <a href="#" onclick="navigate('register')">Register</a>
      </div>
    </div>
  </div>`;

  window.doLogin = async () => {
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    const btn = document.getElementById('loginBtn');
    if (!email || !password) { showToast('Please fill all fields', 'error'); return; }
    btn.disabled = true; btn.textContent = 'Signing in...';
    try {
      const data = await API.login({ email, password });
      if (data.token) localStorage.setItem('token', data.token);
      const user = data.user || data;
      window.appState.user = user;
      localStorage.setItem('user', JSON.stringify(user));
      updateNavForUser(user);
      showToast(`Welcome back, ${user.name || user.email}! 🎉`, 'success');
      navigate('home');
    } catch (e) {
      showToast(e.message || 'Login failed', 'error');
      btn.disabled = false; btn.textContent = 'Sign In';
    }
  };

  ['loginEmail','loginPassword'].forEach(id =>
    document.getElementById(id).addEventListener('keydown', e => { if(e.key==='Enter') window.doLogin(); })
  );
}
