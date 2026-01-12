<template>
  <div class="pokemon-list-container">
    <PokemonFilter
      :filtered-count="filteredPokemons.length"
      @filter="applyFilters"
      @sort="applySort"
      ref="filter"
      class="pokemon-filter"
    />

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Carregando Pokémon...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>Erro ao carregar Pokémon: {{ error }}</p>
      <button @click="loadPokemons" class="btn-retry">Tentar novamente</button>
    </div>

    <div v-else>
      <div class="list-controls">
        <div class="view-toggle">
          <button @click="viewMode = 'grid'" :class="{ active: viewMode === 'grid' }">
            🏠 Grid
          </button>
        </div>

        <div class="pagination-info">
          <span>Página {{ currentPage }} de {{ totalPages }}</span>
          <span>{{ filteredPokemons.length }} Pokémon(s)</span>
        </div>
      </div>

      <div class="list-header">
        <h2>Pokémons</h2>
        <button @click="addNewPokemon" class="btn-add">+ Novo Pokémon</button>
      </div>

      <div :class="['pokemon-list', viewMode]">
        <PokemonCard
          v-for="pokemon in paginatedPokemons"
          :key="pokemon.codigo"
          :pokemon="pokemon"
          :show-actions="showActions"
          @select="onSelectPokemon"
          @edit="onEditPokemon"
          @delete="onDeletePokemon"
        />
      </div>

      <div v-if="filteredPokemons.length > itemsPerPage" class="pagination">
        <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">← Anterior</button>

        <div class="page-numbers">
          <button
            v-for="page in visiblePages"
            :key="page"
            @click="goToPage(page)"
            :class="{ active: page === currentPage }"
            class="page-number"
          >
            {{ page }}
          </button>
        </div>

        <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">
          Próxima →
        </button>
      </div>

      <div v-if="selectedPokemon" class="pokemon-detail">
        <h3>Detalhes: {{ selectedPokemon.nome }}</h3>
        <pre>{{ JSON.stringify(selectedPokemon, null, 2) }}</pre>
        <button @click="selectedPokemon = null" class="btn-close">Fechar</button>
      </div>
    </div>
  </div>

  <div class="pokemon-list-container">

    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Editar Pokémon</h3>

        <form @submit.prevent="savePokemon">
          <div class="form-group">
            <label>Nome:</label>
            <input v-model="editingPokemon.nome" required />
          </div>

          <div class="form-group">
            <label>Código:</label>
            <input v-model="editingPokemon.codigo" tipo="number" required />
          </div>

          <div class="form-group">
            <label>Tipo Primário:</label>
            <select v-model="editingPokemon.tipoPrimario" required>
              <option value="">Selecione...</option>
              <option v-for="tipo in allTipos" :key="tipo" :value="tipo">
                {{ tipo }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Tipo Secundário:</label>
            <select v-model="editingPokemon.tipoSecundario">
              <option value="">Nenhum</option>
              <option v-for="tipo in allTipos" :key="tipo" :value="tipo">
                {{ tipo }}
              </option>
            </select>
          </div>

          <div class="modal-actions">
            <button tipo="submit" class="btn-save">Salvar</button>
            <button tipo="button" @click="cancelEdit" class="btn-cancel">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div class="header-actions">
    <button @click="showTipoManager = true" class="btn-manage-tipos">
      Gerenciar Tipos
    </button>
  </div>

  <Tipomanager
    v-if="showTipoManager"
    @fechar="showTipoManager = false"
    @tipos-atualizados="atualizarTiposCache"
  />
</template>

<script>
import PokemonFilter from './PokemonFilter.vue';
import PokemonCard from './PokemonCard.vue';
import pokemonService from '../services/pokemonService';
import tipoService from '../services/tipoService';
import Tipomanager from './Tipomanager.vue';

export default {
  name: 'PokemonList',
  emits: ['select', 'edit', 'delete'],
  components: {
    PokemonFilter,
    PokemonCard,
    Tipomanager: Tipomanager,
  },
  props: {
    showActions: {
      tipo: Boolean,
      default: false
    },
    itemsPerPage: {
      tipo: Number,
      default: 20
    }
  },
  data() {
    return {
      loading: false,
      error: null,
      allPokemons: [],
      filteredPokemons: [],
      allTipos: [],
      viewMode: 'grid',
      selectedPokemon: null,
      currentPage: 1,
      searchNome: '',
      searchTipo: '',
      sortAsc: true,
      showEditModal: false,
      editingPokemon: null,
      showTipoManager: false
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredPokemons.length / this.itemsPerPage);
    },
    paginatedPokemons() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filteredPokemons.slice(start, end);
    },
    visiblePages() {
      const pages = [];
      const maxVisible = 5;
      let start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
      let end = Math.min(this.totalPages, start + maxVisible - 1);

      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
      }

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      return pages;
    }
  },
  methods: {
    async loadPokemons() {
      this.loading = true;
      this.error = null;

      try {
        console.log('1. loadPokemons iniciado');
        const response = await pokemonService.getAll();

        console.log('2. Resposta da API:', {
          response,
          data: response.data,
          isArray: Array.isArray(response.data),
          tipo: typeof response.data,
          length: response.data?.length
        });

        this.allPokemons = response.data;

        console.log('3. allPokemons atribuído:', {
          value: this.allPokemons,
          isArray: Array.isArray(this.allPokemons),
          hasForEach: typeof this.allPokemons?.forEach === 'function'
        });

        console.log(' primeiro pokemon complto:', this.allPokemons[0]);
        console.log(' Estrutura:', {
          nome: this.allPokemons[0]?.nome,
          codigo: this.allPokemons[0]?.codigo,
          tipoPrimario: this.allPokemons[0]?.tipoPrimario,
          tipoSecundario: this.allPokemons[0]?.tipoSecundario,
          todasAsChaves: Object.keys(this.allPokemons[0] || {})
        });

        this.filteredPokemons = [...(this.allPokemons || [])];

        console.log('4. Chamando metodo extrcyTipos()...');
        this.extractTipos();

      } catch (error) {
        console.error(' ERRO em loadPokemons:', error);
        this.error = error.message;
      } finally {
        this.loading = false;
      }
    },

    extractTipos() {
      const tipos = new Set();
      this.allPokemons.forEach(pokemon => {
        tipos.add(pokemon.tipoPrimario);
        if (pokemon.tipoSecundario) {
          tipos.add(pokemon.tipoSecundario);
        }
      });
      this.allTipos = Array.from(tipos).sort();
    },

    applyFilters(filters) {
      this.searchNome = filters.name;
      this.searchTipo = filters.tipo;
      this.sortAsc = filters.sortAsc;
      this.filterPokemons();
      this.currentPage = 1;
    },

    applySort(sortAsc) {
      this.sortAsc = sortAsc;
      this.filterPokemons();
    },

    filterPokemons() {
      let filtered = [...this.allPokemons];

      if (this.searchNome) {
        const searchTerm = this.searchNome.toLowerCase();
        filtered = filtered.filter(p =>
          p.nome.toLowerCase().includes(searchTerm)
        );
      }

      if (this.searchTipo) {
        filtered = filtered.filter(p =>
          p.tipoPrimario === this.searchTipo ||
          p.tipoSecundario === this.searchTipo
        );
      }

      filtered.sort((a, b) => {
        const nameA = a.nome.toLowerCase();
        const nameB = b.nome.toLowerCase();
        return this.sortAsc
          ? nameA.localeCompare(nameB)
          : nameB.localeCompare(nameA);
      });

      this.filteredPokemons = filtered;
    },

    onSelectPokemon(pokemon) {
      this.selectedPokemon = pokemon;
      this.$emit('select', pokemon);
    },

    onEditPokemon(pokemon) {
      this.editingPokemonOriginal = { ...pokemon };

      this.editingPokemon = {
        codigo: pokemon.codigo,
        nome: pokemon.nome,
        tipoPrimario: pokemon.tipo_primario?.nome || pokemon.tipoPrimario,
        tipoSecundario: pokemon.tipo_secundario?.nome || pokemon.tipoSecundario
      };

      console.log('Original guardado:', this.editingPokemonOriginal.codigo);
      this.showEditModal = true;
    },

    addNewPokemon() {
      this.editingPokemon = {
        nome: '',
        codigo: null,
        tipoPrimario: '',
        tipoSecundario: ''
      };
      this.showEditModal = true;
    },

    async savePokemon() {
      if (!this.editingPokemon) {
        alert('Erro: Nenhum Pokemon para salvar');
        return;
      }

      this.saving = true;

      try {
        console.log('Iniciando salvamento...');

        if (!this.editingPokemon.nome?.trim()) {
          alert('Nome é obrigatorio');
          return;
        }

        if (!this.editingPokemon.tipoPrimario) {
          alert('Tipo primario é obrigatório');
          return;
        }

        const novoCodigo = Number(this.editingPokemon.codigo);
        if (!novoCodigo || novoCodigo < 1) {
          alert('Código inválido. deve ser um número positivo.');
          return;
        }

        const isEditando = !!this.editingPokemonOriginal;
        console.log(isEditando ? ' editando Pokémon' : ' craindp novo Pokémon');

        if (isEditando) {
          console.log(`🔍 Codigo original: ${this.editingPokemonOriginal.codigo}`);
        }

        if (!this.tiposCache) {
          console.log('Carregando tipos...');
          const tiposResponse = await tipoService.getAll();
          this.tiposCache = tiposResponse.data;
          console.log(`${this.tiposCache.length} tipos carregados`);
        }

        const tipoPrimarioObj = this.tiposCache.find(t =>
          t.nome.toLowerCase() === this.editingPokemon.tipoPrimario?.toLowerCase()
        );

        const tipoSecundarioObj = this.editingPokemon.tipoSecundario
          ? this.tiposCache.find(t =>
            t.nome.toLowerCase() === this.editingPokemon.tipoSecundario?.toLowerCase()
          )
          : null;

        if (!tipoPrimarioObj) {
          alert(`Tipo "${this.editingPokemon.tipoPrimario}" não encontrado!`);
          return;
        }

        try {
          await pokemonService.getById(novoCodigo);


          if (isEditando && novoCodigo === this.editingPokemonOriginal.codigo) {
            console.log(` Editando mesmo Pokémon (código ${novoCodigo})`);
          } else {
            alert(` Código ${novoCodigo} já está em uso!`);
            return;
          }
        } catch (error) {
          if (error.response?.status === 404) {
            console.log(`Código ${novoCodigo} disponível!`);
          } else {
            console.error('Erro de verificar código:', error);
          }
        }

        const dadosParaEnviar = {
          codigo: novoCodigo,
          nome: this.editingPokemon.nome.trim(),
          tipo_primario: tipoPrimarioObj,
          tipo_secundario: tipoSecundarioObj
        };

        console.log('Dados para envio:', dadosParaEnviar);

        let response;

        if (isEditando) {
          const codigoAntigo = this.editingPokemonOriginal.codigo;
          console.log(`Editando: PUT /pokemons/${codigoAntigo}`);

          response = await pokemonService.update(codigoAntigo, dadosParaEnviar);

          if (codigoAntigo !== novoCodigo) {
            console.log(` Código alterado: ${codigoAntigo} → ${novoCodigo}`);
          }
        } else {
          console.log(` Criando: POST /pokemons`);
          response = await pokemonService.create(dadosParaEnviar);
        }

        console.log('Sucesso! Status:', response.status);

        await this.loadPokemons();
        this.cancelEdit();

        alert(isEditando ? 'Pokémon atualizado!' : 'Pokémon criado!');

      } catch (error) {
        console.error('Erro ao salvar:', error.response?.data || error.message);
        alert('Erro: ' + (error.response?.data?.message || error.message));
      } finally {
        this.saving = false;
      }
    },

    cancelEdit() {
      this.showEditModal = false;
      this.editingPokemon = null;
      this.editingPokemonOriginal = null;
      this.saving = false;
      console.log('Modal fechado, dados limpos');
    },


    async onDeletePokemon(id) {
      if (confirm('Tem certeza que deseja excluir este Pokémon?')) {
        try {
          await pokemonService.delete(id);
          await this.loadPokemons();
          this.$emit('delete', id);
        } catch (error) {
          alert('Erro ao excluir Pokémon: ' + error.message);
        }
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },

    goToPage(page) {
      this.currentPage = page;
    },

    atualizarTiposCache() {
      console.log(' Tipos foram atualizxados, cleando cache...');
      this.tiposCache = null;
      this.carregarTiposCache();
    },
    async carregarTiposCache() {
      if (!this.tiposCache) {
        const response = await tipoService.getAll();
        this.tiposCache = response.data;
        this.allTipos = this.tiposCache.map(t => t.nome).sort();
      }
    },
  },
  mounted() {
    this.loadPokemons();
  }
}
</script>

<style scoped>
.btn-manage-tipos{
  background: #4c98af;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-manage-tipos:hover {
  background: #347a67;
}

.pokemon-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.error {
  text-align: center;
  padding: 40px;
  color: #f44336;
}

.btn-retry {
  margin-top: 20px;
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.list-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.view-toggle {
  display: flex;
  gap: 10px;
}

.view-toggle button {
  padding: 8px 16px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.view-toggle button.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.pagination-info {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 0.9rem;
}

.pokemon-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  margin-bottom: 30px;
}

.pokemon-list.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}


.pokemon-list.list .pokemon-card {
  width: 100%;
  max-width: 100%;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn:not(:disabled):hover {
  background: #f0f0f0;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 40px;
  height: 40px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-number.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.page-number:hover:not(.active) {
  background: #f0f0f0;
}

.pokemon-detail {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  z-index: 1000;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow: auto;
}

.btn-close {
  margin-top: 20px;
  padding: 10px 20px;
  background: #666;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  min-width: 400px;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn-save {
  background: #4caf50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  flex: 1;
}

.btn-cancel {
  background: #f44336;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  flex: 1;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.btn-add {
  background: #4caf50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-add:hover {
  background: #45a049;
}
</style>
