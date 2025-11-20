import React, {createContext, useContext, useEffect, useState} from 'react'

const CartContext = createContext()

export function CartProvider({children}) {
    const [items, setItems] = useState(() => {
        try {
            return JSON.parse(localStorage.getItem('cart') || '[]')
        } catch {
            return []
        }
    })

    useEffect(() => {
        localStorage.setItem('cart', JSON.stringify(items))
    }, [items])

    const addItem = (item) => setItems(prev => [...prev, item])
    const removeItem = (holdId) => setItems(prev => prev.filter(i => i.holdId !== holdId))
    const clear = () => setItems([])

    return (
        <CartContext.Provider value={{items, addItem, removeItem, clear}}>
            {children}
        </CartContext.Provider>
    )
}

export const useCart = () => useContext(CartContext)
