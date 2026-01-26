/**
 * Common JavaScript utilities for Workday Application
 */

// Utility functions
const Utils = {
    // Format date to Danish format
    formatDate: function(date) {
        return new Date(date).toLocaleDateString('da-DK', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },
    
    // Format currency
    formatCurrency: function(amount) {
        return new Intl.NumberFormat('da-DK', {
            style: 'currency',
            currency: 'DKK'
        }).format(amount);
    },
    
    // Show confirmation dialog
    confirmAction: function(message, callback) {
        if (confirm(message)) {
            callback();
        }
    },
    
    // Toggle element visibility
    toggleElement: function(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.style.display = element.style.display === 'none' ? 'block' : 'none';
        }
    },
    
    // Debounce function for search inputs
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
};

// Form validation
const FormValidator = {
    validateEmail: function(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    },
    
    validateRequired: function(field) {
        return field.value.trim() !== '';
    },
    
    validateNumber: function(field, min = 0, max = Infinity) {
        const value = parseFloat(field.value);
        return !isNaN(value) && value >= min && value <= max;
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('Workday Application initialized');
    
    // Add global error handling
    window.addEventListener('error', function(e) {
        console.error('Global error:', e.error);
    });
    
    // Add form validation listeners
    const forms = document.querySelectorAll('form[data-validate]');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!FormValidator.validateForm(form)) {
                e.preventDefault();
            }
        });
    });
});
