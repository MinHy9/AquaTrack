export const API_BASE = location.origin.includes('localhost')
    ? 'http://localhost:8080'
    : location.origin;

const token = localStorage.getItem('token') || sessionStorage.getItem('token');

export const AUTH_HEADER = {
    'Authorization': `Bearer ${token}`
};