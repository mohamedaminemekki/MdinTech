// Basculer le mode sombre
const toggleDarkMode = () => {
    document.body.classList.toggle('dark-mode');
};

// Ajouter un bouton pour basculer le mode sombre
const darkModeButton = document.createElement('button');
darkModeButton.textContent = '🌙 Mode Sombre';
darkModeButton.addEventListener('click', toggleDarkMode);
document.body.appendChild(darkModeButton);

// Gestionnaire d'événement pour le bouton de recherche
document.getElementById('searchButton').addEventListener('click', () => {
    const query = document.getElementById('searchInput').value.trim();
    if (query) {
        alert(`Recherche lancée : ${query}`);
    } else {
        alert('Veuillez entrer un terme de recherche.');
    }
});

// Gestionnaire d'événement pour le bouton d'effacement
document.getElementById('clearButton').addEventListener('click', () => {
    document.getElementById('searchInput').value = '';
    document.getElementById('searchInput').focus();
});