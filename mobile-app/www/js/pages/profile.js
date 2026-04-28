import API from '../api.js';

export async function renderProfile(container) {
  const user = window.appState.user;
  if (!user) { container.innerHTML = `<div class="page empty-state"><div class="empty-icon">🔑</div><h3>Not logged in</h3><p>Please login to view your profile.</p><button class="btn btn-primary" onclick="navigate('login')">Login</button></div>`; return; }

  let profile = user;
  try { profile = await API.myProfile(); } catch {}

  container.innerHTML = `<div class="page">
    <div class="profile-header">
      <div class="profile-avatar">👤</div>
      <div class="profile-name">${profile.name || user.name}</div>
      <div class="profile-email">${profile.email || user.email}</div>
      <span class="profile-role">${(profile.role || user.role || '').toUpperCase()}</span>
    </div>
    <div class="form-card">
      <div class="form-title" style="font-size:16px;margin-bottom:16px">Edit Profile</div>
      <div class="form-group">
        <label>Full Name</label>
        <input id="profName" type="text" value="${profile.name || ''}"/>
      </div>
      <div class="form-group">
        <label>Phone</label>
        <input id="profPhone" type="tel" placeholder="+1 234 567 8900" value="${profile.phone || ''}"/>
      </div>
      <div class="form-group">
        <label>Location</label>
        <input id="profLocation" type="text" placeholder="City, Country" value="${profile.location || ''}"/>
      </div>
      <div class="form-group">
        <label>Bio / About Me</label>
        <textarea id="profBio" placeholder="Tell employers about yourself...">${profile.bio || ''}</textarea>
      </div>
      <div class="form-group">
        <label>Skills (comma separated)</label>
        <input id="profSkills" type="text" placeholder="React, Node.js, Python..." value="${profile.skills || ''}"/>
      </div>
      <div class="form-group">
        <label>LinkedIn URL</label>
        <input id="profLinkedin" type="url" placeholder="https://linkedin.com/in/..." value="${profile.linkedinUrl || ''}"/>
      </div>
      <div class="form-group">
        <label>Portfolio URL</label>
        <input id="profPortfolio" type="url" placeholder="https://yoursite.com" value="${profile.portfolioUrl || ''}"/>
      </div>
      <button class="btn btn-primary btn-block" id="saveProfileBtn" onclick="saveProfile()">Save Changes</button>
    </div>
    <br/>
    <button class="btn btn-danger btn-block" onclick="logout()">🚪 Logout</button>
    <br/>
  </div>`;

  window.saveProfile = async () => {
    const btn = document.getElementById('saveProfileBtn');
    btn.disabled = true; btn.textContent = 'Saving...';
    try {
      await API.updateProfile({
        name: document.getElementById('profName').value,
        phone: document.getElementById('profPhone').value,
        location: document.getElementById('profLocation').value,
        bio: document.getElementById('profBio').value,
        skills: document.getElementById('profSkills').value,
        linkedinUrl: document.getElementById('profLinkedin').value,
        portfolioUrl: document.getElementById('profPortfolio').value,
      });
      showToast('Profile updated! ✅', 'success');
    } catch (e) {
      showToast(e.message || 'Update failed', 'error');
    } finally {
      btn.disabled = false; btn.textContent = 'Save Changes';
    }
  };
}
