import api from './api.js'

const tipoService = {
  async getAll() {
    return await api.get('/tipos')
  },

  getById(id) {
    return api.get(`/tipos/${id}`);
  },

  create(tipo) {
    return api.post('/tipos', tipo);
  },

  update(id, tipo) {
    return api.put(`/tipos/${id}`, tipo);
  },

  delete(id) {
    return api.delete(`/tipos/${id}`);
  }
};

export default tipoService;
