package eu.glatz.sifidraw.controller

import eu.glatz.sifidraw.config.ProjectSettings
import eu.glatz.sifidraw.model.FilterSet
import eu.glatz.sifidraw.repository.FilterSetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class FilterSetController @Autowired constructor(
        private val filterSetRepository: FilterSetRepository,
        private val projectSettings: ProjectSettings) {

    @GetMapping("/filters")
    fun getFilterSets(): List<FilterSet> {
        return filterSetRepository.findAll();
    }

    @PostMapping("/filters/create")
    fun createImageData(@RequestBody filterSet: FilterSet) {
        filterSetRepository.save(filterSet);
    }

    @DeleteMapping("/filters/delete/{id}")
    fun deleteImageData(@PathVariable id: Long) {
        val obj = filterSetRepository.findById(id);
        if (!obj.isPresent)
            return
        filterSetRepository.delete(obj.get())
    }

    @PutMapping("/filters/save")
    fun modifyImageData(@RequestBody filterSet: FilterSet): FilterSet {
        return filterSetRepository.save(filterSet);
    }
}