// Main JavaScript for Food Delivery System

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initTooltips();
    initFormValidation();
    initAnimations();
    initRestaurantSearch();
    initAccountDropdown();
});

function initAccountDropdown() {
    const accounts = document.querySelectorAll('.nav-account');
    if (!accounts.length) return;

    accounts.forEach(account => {
        const toggle = account.querySelector('.nav-account-toggle');
        if (!toggle) return;

        toggle.addEventListener('click', function (e) {
            e.preventDefault();
            const isOpen = account.classList.contains('open');

            // close any other open account menus
            document.querySelectorAll('.nav-account.open').forEach(a => {
                if (a !== account) a.classList.remove('open');
            });

            if (isOpen) {
                account.classList.remove('open');
            } else {
                account.classList.add('open');
            }
        });
    });

    // close when clicking outside
    document.addEventListener('click', function (e) {
        if (!e.target.closest('.nav-account')) {
            document.querySelectorAll('.nav-account.open').forEach(a => a.classList.remove('open'));
        }
    });
}

// Tooltips
function initTooltips() {
    const tooltips = document.querySelectorAll('[data-tooltip]');
    tooltips.forEach(element => {
        element.addEventListener('mouseenter', function() {
            const tooltip = document.createElement('div');
            tooltip.className = 'tooltip';
            tooltip.textContent = this.getAttribute('data-tooltip');
            document.body.appendChild(tooltip);
            
            const rect = this.getBoundingClientRect();
            tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
            tooltip.style.top = rect.top - tooltip.offsetHeight - 10 + 'px';
            
            this.addEventListener('mouseleave', function() {
                tooltip.remove();
            }, { once: true });
        });
    });
}

// Form Validation
function initFormValidation() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const inputs = this.querySelectorAll('input[required], select[required]');
            let isValid = true;
            
            inputs.forEach(input => {
                if (!input.value.trim()) {
                    isValid = false;
                    input.style.borderColor = '#e74c3c';
                } else {
                    input.style.borderColor = '#27ae60';
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                showAlert('Please fill in all required fields', 'error');
            }
        });
    });
}

// Animations
function initAnimations() {
    const cards = document.querySelectorAll('.stat-card, .card, .restaurant-card');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    });
    
    cards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.5s, transform 0.5s';
        observer.observe(card);
    });
}

// Quick restaurant search on Restaurants page
function initRestaurantSearch() {
    const input = document.getElementById('restaurantSearchInput');
    if (!input) return;

    input.addEventListener('input', filterRestaurants);
}

function filterRestaurants() {
    const input = document.getElementById('restaurantSearchInput');
    if (!input) return;

    const info = document.getElementById('restaurantSearchInfo');
    const query = input.value.toLowerCase();
    const cards = document.querySelectorAll('.restaurant-grid .restaurant-card');
    let visibleCount = 0;

    cards.forEach(card => {
        const name = (card.getAttribute('data-name') || '').toLowerCase();
        const cuisine = (card.getAttribute('data-cuisine') || '').toLowerCase();
        const address = (card.getAttribute('data-address') || '').toLowerCase();
        if (!query || name.includes(query) || cuisine.includes(query) || address.includes(query)) {
            card.style.display = '';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });

    if (info) {
        if (!query) {
            info.textContent = '';
        } else if (visibleCount === 0) {
            info.textContent = `No restaurants match "${input.value}".`;
        } else {
            info.textContent = `${visibleCount} restaurant(s) found.`;
        }
    }
}

// Show Alert
function showAlert(message, type = 'info') {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    
    document.body.insertBefore(alert, document.body.firstChild);
    
    setTimeout(() => {
        alert.style.opacity = '0';
        setTimeout(() => alert.remove(), 300);
    }, 3000);
}

// Format Currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

// Format Date
function formatDate(dateString) {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
}

// Confirm Action
function confirmAction(message) {
    return confirm(message || 'Are you sure?');
}

// Loading Spinner
function showLoading() {
    const spinner = document.createElement('div');
    spinner.id = 'loading-spinner';
    spinner.innerHTML = '<div class="spinner"></div>';
    document.body.appendChild(spinner);
}

function hideLoading() {
    const spinner = document.getElementById('loading-spinner');
    if (spinner) spinner.remove();
}

