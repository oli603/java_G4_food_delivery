// Dashboard specific JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initCharts();
    initSearch();
    animateStats();
});

// Initialize Charts
function initCharts() {
    // Order Status Chart
    const orderCtx = document.getElementById('orderChart');
    if (orderCtx) {
        new Chart(orderCtx, {
            type: 'doughnut',
            data: {
                labels: ['New', 'Preparing', 'Delivered', 'Cancelled'],
                datasets: [{
                    data: [12, 8, 45, 3],
                    backgroundColor: [
                        '#3498db',
                        '#9b59b6',
                        '#27ae60',
                        '#e74c3c'
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }
    
    // Revenue Chart
    const revenueCtx = document.getElementById('revenueChart');
    if (revenueCtx) {
        new Chart(revenueCtx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
                datasets: [{
                    label: 'Revenue (ETB)',
                    data: [1200, 1900, 3000, 2500, 2800, 3500],
                    borderColor: '#ff6b35',
                    backgroundColor: 'rgba(255, 107, 53, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: true
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
}

// Search Functionality
function initSearch() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }
}

function performSearch() {
    const query = document.getElementById('searchInput').value;
    const resultsDiv = document.getElementById('searchResults');
    
    if (!query.trim()) {
        resultsDiv.innerHTML = '<p class="alert alert-info">Please enter a search term</p>';
        return;
    }
    
    resultsDiv.innerHTML = '<div class="loading">Searching...</div>';
    
    // Simulate search (replace with actual AJAX call)
    setTimeout(() => {
        resultsDiv.innerHTML = `
            <div class="search-result-item">
                <h4>🍽️ Restaurant: ${query}</h4>
                <p>Found matching restaurants</p>
            </div>
            <div class="search-result-item">
                <h4>👤 User: ${query}</h4>
                <p>Found matching users</p>
            </div>
        `;
    }, 500);
}

// Animate Stats
function animateStats() {
    const stats = document.querySelectorAll('.stat-info h3');
    stats.forEach(stat => {
        const finalValue = stat.textContent;
        const numericValue = parseFloat(finalValue.replace(/[^0-9.]/g, ''));
        
        if (!isNaN(numericValue)) {
            let current = 0;
            const increment = numericValue / 50;
            const timer = setInterval(() => {
                current += increment;
                if (current >= numericValue) {
                    stat.textContent = finalValue;
                    clearInterval(timer);
                } else {
                    if (finalValue.includes('ETB')) {
                        stat.textContent = current.toFixed(2) + ' ETB';
                    } else {
                        stat.textContent = Math.floor(current);
                    }
                }
            }, 20);
        }
    });
}

// Real-time Updates
function updateDashboard() {
    // This would fetch real-time data via AJAX
    console.log('Updating dashboard...');
}

// Auto-refresh every 30 seconds
setInterval(updateDashboard, 30000);

