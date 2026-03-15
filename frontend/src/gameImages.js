const GAME_IMAGES = {
    'Brass: Birmingham': 'https://cf.geekdo-images.com/pq9UWUrAtVsBusaFDOsQGw__imagepage/img/dDPqDq5B8LoGYqCJqUyNGUuQxT8=/fit-in/900x600/filters:no_upscale():strip_icc()/pic5822713.jpg',
    'Ark Nova': 'https://cf.geekdo-images.com/_fVrJowAKIv10VnMUznyuA__imagepage/img/O_Ab86r6FGuyaPHmb_Ayep4yQTY=/fit-in/900x600/filters:no_upscale():strip_icc()/pic7027509.jpg',
    'Pandemic Legacy: Season 1': 'https://cf.geekdo-images.com/large/img/0Ddrd-mWsN3BVgIOYkyNA-Uwc4o=/fit-in/1024x1024/filters:no_upscale()/pic3476620.jpg',
    'Gloomhaven': 'https://cf.geekdo-images.com/YE7WFhd21VDeVqXs602IUQ__imagepage/img/3KDj9KTT2svAZZMoZ8gqoOmTAqQ=/fit-in/900x600/filters:no_upscale():strip_icc()/pic7603900.png',
    'Dune: Imperium - Uprising': 'https://cf.geekdo-images.com/YWHB6f-fyVwjSyXh5nK2Xw__imagepage/img/pNvO3c-GhSglpu56151oWXuG4ng=/fit-in/900x600/filters:no_upscale():strip_icc()/pic6267299.png',
    'Dune: Imperium': 'https://cf.geekdo-images.com/YWHB6f-fyVwjSyXh5nK2Xw__imagepage/img/pNvO3c-GhSglpu56151oWXuG4ng=/fit-in/900x600/filters:no_upscale():strip_icc()/pic6267299.png',
    'Twilight Imperium: Fourth Edition': 'https://cf.geekdo-images.com/xNFCiOAXp0k5qvXQUmfQ6Q__imagepagezoom/img/-k_cJNOspRgs-LTsnjgAtlz6D7w=/fit-in/1200x900/filters:no_upscale():strip_icc()/pic3690829.png',
    'War of the Ring: Second Edition': 'https://cf.geekdo-images.com/ZH1CMiL5PU--JiXi2QLomQ__imagepage/img/hUT8DuCRP6iDsuEzAE4uCJJHEKo=/fit-in/900x600/filters:no_upscale():strip_icc()/pic6598055.jpg',
    'Terraforming Mars': 'https://cf.geekdo-images.com/wg9oOLcsKvDesSUdZQ4rxw__original/img/thIqWDnH9utKuoKVEUqveDixprI=/0x0/filters:format(jpeg)/pic3536616.jpg',
    'Star Wars: Rebellion': 'https://cf.geekdo-images.com/mrWFkQVEHsZggcf9thK37A__original/img/tnJfnozbnP28keKGOOnoczjpFlo=/0x0/filters:format(jpeg)/pic3006954.jpg',
    'Catan': 'https://cf.geekdo-images.com/W3Bsga_uLP9kO91gZ7H8yw__original/img/xV7oisd3RQ8R-k18cdWAYthHXsA=/0x0/filters:format(jpeg)/pic2419375.jpg',
    'Wingspan': 'https://cf.geekdo-images.com/yLZJCVLlIx4c7eJEWUNJ7w__original/img/cI782Zis9cT66j2MjSHKJGnFPNw=/0x0/filters:format(jpeg)/pic4458123.jpg'
}

export function getGameImage(title) {
    if (!title) return null
    const url = GAME_IMAGES[title]
    return url && url.trim() ? url : null
}
