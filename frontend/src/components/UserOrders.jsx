import React, { useState, useEffect } from 'react'
import api from '../api'

export default function UserOrders() {
    const [userOrders, setUserOrders] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [userIdFilter, setUserIdFilter] = useState('')
    const [statusFilter, setStatusFilter] = useState('ALL')
    const [showCreateForm, setShowCreateForm] = useState(false)
    const [createFormData, setCreateFormData] = useState({
        userId: '',
        items: [],
        status: 'PENDING'
    })

    useEffect(() => {
        fetchUserOrders()
    }, [userIdFilter, statusFilter])

    const fetchUserOrders = async () => {
        try {
            setLoading(true)
            let data
            
            if (userIdFilter) {
                if (statusFilter !== 'ALL') {
                    data = await api.getUserOrdersByUserIdAndStatus(userIdFilter, statusFilter)
                } else {
                    data = await api.getUserOrders(userIdFilter)
                }
            } else {
                // For now, we'll get all orders if no user filter is applied
                // In a real app, we might want a separate endpoint for all orders
                data = []
            }
            
            setUserOrders(data || [])
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    const handleCreateOrder = async (e) => {
        e.preventDefault()
        
        try {
            const orderData = {
                ...createFormData,
                userId: createFormData.userId,
                items: JSON.parse(createFormData.items),
                status: createFormData.status
            }
            
            await api.createUserOrder(orderData)
            setCreateFormData({ userId: '', items: '[]', status: 'PENDING' })
            setShowCreateForm(false)
            fetchUserOrders()
        } catch (err) {
            setError(err.message)
        }
    }

    const handleCancelOrder = async (orderId) => {
        try {
            await api.cancelUserOrder(orderId)
            fetchUserOrders()
        } catch (err) {
            setError(err.message)
        }
    }

    const handleUpdateStatus = async (orderId, status) => {
        try {
            await api.updateUserOrderStatus(orderId, status)
            fetchUserOrders()
        } catch (err) {
            setError(err.message)
        }
    }

    if (loading) return <div className="loading">Loading user orders...</div>
    if (error) return <div className="error">Error: {error}</div>

    return (
        <div className="user-orders">
            <h2>User Orders</h2>
            
            <div className="filters">
                <input
                    type="text"
                    placeholder="Filter by User ID"
                    value={userIdFilter}
                    onChange={(e) => setUserIdFilter(e.target.value)}
                />
                <select 
                    value={statusFilter} 
                    onChange={(e) => setStatusFilter(e.target.value)}
                >
                    <option value="ALL">All Statuses</option>
                    <option value="PENDING">Pending</option>
                    <option value="CONFIRMED">Confirmed</option>
                    <option value="SHIPPED">Shipped</option>
                    <option value="DELIVERED">Delivered</option>
                    <option value="CANCELLED">Cancelled</option>
                </select>
                <button onClick={() => setShowCreateForm(!showCreateForm)}>
                    {showCreateForm ? 'Cancel' : 'Create New Order'}
                </button>
            </div>

            {showCreateForm && (
                <form onSubmit={handleCreateOrder} className="create-order-form">
                    <input
                        type="text"
                        placeholder="User ID"
                        value={createFormData.userId}
                        onChange={(e) => setCreateFormData({...createFormData, userId: e.target.value})}
                        required
                    />
                    <textarea
                        placeholder="Items (JSON array)"
                        value={createFormData.items}
                        onChange={(e) => setCreateFormData({...createFormData, items: e.target.value})}
                        required
                    />
                    <select
                        value={createFormData.status}
                        onChange={(e) => setCreateFormData({...createFormData, status: e.target.value})}
                    >
                        <option value="PENDING">Pending</option>
                        <option value="CONFIRMED">Confirmed</option>
                        <option value="SHIPPED">Shipped</option>
                        <option value="DELIVERED">Delivered</option>
                        <option value="CANCELLED">Cancelled</option>
                    </select>
                    <button type="submit">Create Order</button>
                </form>
            )}

            <div className="user-orders-list">
                {userOrders.map(order => (
                    <div key={order.id} className="user-order-card">
                        <h3>Order ID: {order.id}</h3>
                        <p>User ID: {order.userId}</p>
                        <p>Status: {order.status}</p>
                        <p>Created: {new Date(order.createdAt).toLocaleString()}</p>
                        {order.updatedAt && <p>Updated: {new Date(order.updatedAt).toLocaleString()}</p>}
                        {order.completedAt && <p>Completed: {new Date(order.completedAt).toLocaleString()}</p>}
                        
                        <div className="order-items">
                            <h4>Items:</h4>
                            {order.items.map((item, index) => (
                                <div key={index} className="order-item">
                                    <p>Game ID: {item.gameId}</p>
                                    <p>Title: {item.title}</p>
                                    <p>Quantity: {item.quantity}</p>
                                    <p>Price: ${item.price}</p>
                                </div>
                            ))}
                        </div>
                        
                        <div className="order-actions">
                            {order.status !== 'CANCELLED' && (
                                <>
                                    <button onClick={() => handleUpdateStatus(order.id, 'CONFIRMED')}>
                                        Confirm
                                    </button>
                                    <button onClick={() => handleUpdateStatus(order.id, 'SHIPPED')}>
                                        Ship
                                    </button>
                                    <button onClick={() => handleUpdateStatus(order.id, 'DELIVERED')}>
                                        Deliver
                                    </button>
                                    <button 
                                        onClick={() => handleCancelOrder(order.id)}
                                        className="cancel-btn"
                                    >
                                        Cancel
                                    </button>
                                </>
                            )}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}