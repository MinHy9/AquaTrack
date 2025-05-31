export const API_BASE = location.origin.includes('localhost')
    ? 'http://localhost:8080'
    : location.origin;

export const AUTH_HEADER = {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
};