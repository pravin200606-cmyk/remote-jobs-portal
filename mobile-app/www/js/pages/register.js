import API from '../api.js';

export function renderRegister(container) {
  container.innerHTML = `<div class="page">
    <div class="form-card">
      <div class="form-title">Create Account 🚀</div>
      <div class="form-sub">Join thousands of remote professionals</div>
      <div class="form-group">
        <label>Full Name</label>
        <input id="regName" type="text" placeholder="John Doe" autocomplete="name"/>
      </div>
      <div class="form-group">
        <label>Email Address</label>
        <input id="regEmail" type="email" placeholder="you@example.com" autocomplete="email"/>
      </div>
      <div class="form-group">
        <label>Password</label>
        <input id="regPassword" type="password" placeholder="Min 8 characters" autocomplete="new-password"/>
      </div>
      <div class="form-group">
        <label>I am a...</label>
        <select id="regRole">
          <option value="JOBSEEKER">Job Seeker</option>
          <option value="EMPLOYER">Employer / Recruiter</option>
        </select>
      </div>
      <button class="btn btn-primary btn-block" id="regBtn" onclick="doRegister()">Create Account</button>
      <div class="form-link">
        Already have an account? <a href="#" onclick="navigate('login')">Sign In</a>
      </div>
    </div>
  </div>`;

  window.doRegister = async () => {
    const name = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const role = document.getElementById('regRole').value;
    const btn = document.getElementById('regBtn');
    if (!name || !email || !password) { showToast('Please fill all fields', 'error'); return; }
    if (password.length < 8) { showToast('Password must be at least 8 characters', 'error'); return; }
    btn.disabled = true; btn.textContent = 'Creating account...';
    try {
      const data = await API.register({ name, email, password, role });
      if (data.token) localStorage.setItem('token', data.token);
      const user = data.user || data;
      window.appState.user = user;
      localStorage.setItem('user', JSON.stringify(user));
      updateNavForUser(user);
      showToast('Account created! Welcome! 🎉', 'success');
      navigate('home');
    } catch (e) {
      showToast(e.message || 'Registration failed', 'error');
      btn.disabled = false; btn.textContent = 'Create Account';
    }
  };
}
