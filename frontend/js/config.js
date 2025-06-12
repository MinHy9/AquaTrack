export const API_BASE = location.origin.includes('localhost')
    ? 'http://localhost:8080'
    : 'http://211.188.61.154:8080';

const token = localStorage.getItem('token') || sessionStorage.getItem('token');

export const AUTH_HEADER = {
    'Authorization': `Bearer ${token}`
};