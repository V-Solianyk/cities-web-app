async function startGame() {
    try {
        const response = await fetch('/game/begin');
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Не вдалося почати гру');
        }
        const data = await response.json();
        document.getElementById('response').textContent = 'Система пропонує: ' + data.name;
    } catch (error) {
        document.getElementById('response').textContent = 'Помилка: ' + error.message;
    }
}

async function getNextCity() {
    try {
        const word = document.getElementById('cityInput').value;
        const response = await fetch(`/game/next?word=${word}`);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Не вдалося отримати наступне місто');
        }
        const data = await response.json();
        document.getElementById('response').textContent = 'Система відповідає: ' + data.name;
    } catch (error) {
        document.getElementById('response').textContent = 'Помилка: ' + error.message;
    }
}

async function endGame() {
    try {
        const response = await fetch('/game/end', { method: 'POST' });
        if (!response.ok) {
            throw new Error('Не вдалося закінчити гру');
        }
        const message = await response.text();
        document.getElementById('response').textContent = message;
    } catch (error) {
        document.getElementById('response').textContent = 'Помилка: ' + error.message;
    }
}
