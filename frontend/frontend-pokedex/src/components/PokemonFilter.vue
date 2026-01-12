<template>
  <div class="pokemon-filter">
    <input
      v-model="searchNome"
      @input="applyFilter"
      placeholder="Buscar Pokémon..."
      class="search-input"
    />
    <div v-if="loading" class="loading-indicator">
      Carregando tipos...
    </div>
    <select
      v-else
      v-model="selectedTipo"
      @change="applyFilter"
      class="type-select"
    >
      <option value="">Todos os tipos</option>
      <option v-for="type in tiposList" :key="type" :value="type">
        {{ type }}
      </option>
    </select>

    <button @click="applySort" class="sort-btn">
    {{ sortAsc ? 'A -> Z' : 'Z -> A' }}
    </button>

    <span v-if="filteredCount !== undefined && filteredCount !== null" class="counter">
      {{ filteredCount }} Pokémon(s)
    </span>
  </div>
</template>

<script>
import tipoService from '../services/tipoService';

export default {

  props: {
    filteredCount: {
      tipo: Number,
      default: 0,
      required: false
    }
  },

  data() {
    return {
      searchNome: '',
      selectedTipo: '',
      sortAsc: true,
      tiposList: [],
      loading: false,
      errorTipos: null
    }
  },
  computed: {
    availableTipos() {
      return this.tiposList;
    }
  },
  methods: {
    async loadTipos() {
      const response = await tipoService.getAll();
      this.tiposList = response.data.map(t => t.nome);
      this.tiposMap = response.data.reduce((map, tipo) => {
        map[tipo.nome] = tipo.codigo;
        return map;
      }, {});

      this.$emit('tipos-loaded', this.tiposMap);
    },

    applyFilter() {
      this.$emit('filter', {
        name: this.searchNome,
        tipo: this.selectedTipo,
        sortAsc: this.sortAsc
      });
    },

    applySort() {
      this.sortAsc = !this.sortAsc;
      this.$emit('sort', this.sortAsc);
      this.applyFilter();
    }
  },
  mounted() {
    this.loadTipos();
  }
}
</script>

<style scoped>
.pokemon-filter {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input, .type-select {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}

.search-input {
  min-width: 200px;
}

.type-select {
  min-width: 150px;
}

.sort-btn {
  padding: 8px 16px;
  background: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.sort-btn:hover {
  background: #e0e0e0;
}

.counter {
  font-weight: bold;
  color: #333;
  margin-left: 10px;
}

.loading-indicator {
  padding: 8px 12px;
  color: #666;
  font-style: italic;
  font-size: 14px;
  border: 1px dashed #ccc;
  border-radius: 4px;
}
</style>
