import React, { useState, useEffect } from 'react'
import api from '../api'

export default function Users() {
    const [users, setUsers] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        firstName: '',
        lastName: ''
    })
    const [editingId, setEditingId] = useState(null)

    useEffect(() => {
        fetchUsers()
    }, [])

    const fetchUsers = async () => {
        try {
            setLoading(true)
            const data = await api.getUsers()
            setUsers(data)
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        
        try {
            if (editingId) {
                await api.updateUser(editingId, formData)
            } else {
                await api.createUser(formData)
            }
            
            setFormData({ username: '', email: '', firstName: '', lastName: '' })
            setEditingId(null)
            fetchUsers()
        } catch (err) {
            setError(err.message)
        }
    }

    const handleEdit = (user) => {
        setFormData({
            username: user.username,
            email: user.email,
            firstName: user.firstName,
            lastName: user.lastName
        })
        setEditingId(user.id)
    }

    const handleDelete = async (id) => {
        try {
            await api.deleteUser(id)
            fetchUsers()
        } catch (err) {
            setError(err.message)
        }
    }

    if (loading) return <div className="loading">Loading users...</div>
    if (error) return <div className="error">Error: {error}</div>

    return (
        <div className="users">
            <h2>Users</h2>
            
            <form onSubmit={handleSubmit} className="user-form">
                <input
                    type="text"
                    placeholder="Username"
                    value={formData.username}
                    onChange={(e) => setFormData({...formData, username: e.target.value})}
                    required
                />
                <input
                    type="email"
                    placeholder="Email"
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    required
                />
                <input
                    type="text"
                    placeholder="First Name"
                    value={formData.firstName}
                    onChange={(e) => setFormData({...formData, firstName: e.target.value})}
                />
                <input
                    type="text"
                    placeholder="Last Name"
                    value={formData.lastName}
                    onChange={(e) => setFormData({...formData, lastName: e.target.value})}
                />
                <button type="submit">{editingId ? 'Update User' : 'Create User'}</button>
                {editingId && (
                    <button type="button" onClick={() => {
                        setFormData({ username: '', email: '', firstName: '', lastName: '' })
                        setEditingId(null)
                    }}>
                        Cancel
                    </button>
                )}
            </form>

            <div className="user-list">
                {users.map(user => (
                    <div key={user.id} className="user-card">
                        <h3>{user.firstName} {user.lastName} ({user.username})</h3>
                        <p>Email: {user.email}</p>
                        <p>Created: {new Date(user.createdAt).toLocaleString()}</p>
                        <div className="user-actions">
                            <button onClick={() => handleEdit(user)}>Edit</button>
                            <button onClick={() => handleDelete(user.id)}>Delete</button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}