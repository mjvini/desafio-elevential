<!-- src/App.vue -->
<template>
  <div id="app">
    <header>
      <h1>Pokédex</h1>
      <p>Catalogue seus Pokémon!</p>
    </header>

    <main>
      <PokemonList
        @select="onPokemonSelect"
        @edit="onPokemonEdit"
        @delete="onPokemonDelete"
      />
    </main>

    <footer>
      <p>Desenvolvido por mjvini - Desafio-Elevential </p>
    </footer>
  </div>
</template>

<script>
import PokemonList from './components/PokemonList.vue';

export default {
  name: 'App',
  components: {
    PokemonList
  },
  methods: {
    onPokemonSelect(pokemon) {
      console.log('Pokemon selecionado:', pokemon);
    },
    onPokemonEdit(pokemon) {
      console.log('Editar Pokemon:', pokemon);
    },
    onPokemonDelete(id) {
      console.log('Excluir Pokemon ID:', id);
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
}

#app {
  min-height: 100vh;
  background: linear-gradient(135deg, #ffffff 0%, #c82a2a 100%);
  padding: 20px;
}

header {
  background: linear-gradient(135deg, rgba(246, 201, 201, 0.95) 0%, rgba(223, 53, 53, 0.9) 100%);
  text-align: center;
  padding: 40px 20px;
  border-radius: 25px;
  margin: 20px;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

header h1 {
  color: #201f1f;
  margin-bottom: 10px;
  font-size: 2.5rem;
}

header p {
  color: #252525;
  font-size: 1.1rem;
}

main {
  flex: 1;
  padding: 30px 20px;
}

footer {
  background: rgba(0, 0, 0, 0.8);
  color: white;
  text-align: center;
  padding: 20px;
  font-size: 0.9rem;
}
</style>
