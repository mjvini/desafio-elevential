import api from './api.js';

export default {
  getAll() {
    return api.get('/pokemons');
  },

  getById(id) {
    return api.get(`/pokemons/${id}`);
  },

  create(pokemon) {
    return api.post('/pokemons', pokemon);
  },

  update(id, pokemon) {
    console.log(`🔄 Service.update: id=${id} (antigo), novoCodigo=${pokemon.codigo}`);
    return api.put(`/pokemons/${id}`, pokemon);
  },

  delete(id) {
    return api.delete(`/pokemons/${id}`);
  }
};
