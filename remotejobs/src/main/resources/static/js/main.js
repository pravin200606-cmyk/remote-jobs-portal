// RemoteWork Hub - Main JS

document.addEventListener('DOMContentLoaded', function () {

    // ============ MOBILE NAV TOGGLE ============
    const navToggle = document.getElementById('navToggle');
    const navMenu = document.getElementById('navMenu');
    if (navToggle && navMenu) {
        navToggle.addEventListener('click', () => {
            navMenu.classList.toggle('open');
        });
    }

    // ============ SIDEBAR TOGGLE (Dashboard) ============
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('sidebar');
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });
    }

    // ============ AUTO-DISMISS ALERTS ============
    const alerts = document.querySelectorAll('.alert[data-auto-dismiss]');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 4000);
    });

    // ============ TABLE FILTER (JS-based) ============
    const tableSearch = document.getElementById('tableSearch');
    if (tableSearch) {
        tableSearch.addEventListener('input', function () {
            const q = this.value.toLowerCase();
            const rows = document.querySelectorAll('.filterable-table tbody tr');
            rows.forEach(row => {
                row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
            });
        });
    }

    // ============ CONFIRM DELETE ============
    document.querySelectorAll('[data-confirm]').forEach(btn => {
        btn.addEventListener('click', function (e) {
            const msg = this.getAttribute('data-confirm') || 'Are you sure?';
            if (!confirm(msg)) e.preventDefault();
        });
    });

    // ============ FILE INPUT LABEL UPDATE ============
    document.querySelectorAll('input[type="file"]').forEach(input => {
        input.addEventListener('change', function () {
            const label = document.querySelector(`label[for="${this.id}"]`);
            if (label && this.files[0]) {
                label.textContent = this.files[0].name;
            }
        });
    });

    // ============ CHARACTER COUNTER ============
    document.querySelectorAll('[data-max-chars]').forEach(el => {
        const max = parseInt(el.getAttribute('data-max-chars'));
        const counter = document.createElement('small');
        counter.className = 'text-muted';
        el.parentNode.appendChild(counter);
        const update = () => {
            const remaining = max - el.value.length;
            counter.textContent = `${el.value.length}/${max} characters`;
            counter.style.color = remaining < 50 ? '#ef4444' : '';
        };
        el.addEventListener('input', update);
        update();
    });

    // ============ ACTIVE NAV LINK ============
    const currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar-nav a').forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
});
