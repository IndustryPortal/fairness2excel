# fairness2excel
Transform the json result of the fairness web service to an excel file

## To use
```bash
java -jar bin/fairness2excel.jar -r <stageportal|agroportal|bioportal> -o <acronyms (comma separated) | all> -f <output file path>
```
### Arguments description :
  * r (**repository-name**) : Name of the ontology repository (agroportal, bioportal, stageportal).
  * o (**ontologies**): Acronyms of the ontologies to evaluate (comma separated) or all.
  * f (**output-file**) : **Optional** file output path . Default value "Results/FAIRnessResults-" + portalanme + ".xlsx"
### Exemple of use
  * ```
    java -jar bin/fairness2excel.jar -r stageportal -o FCU,GEMET2 
    ```
  * ``` 
    java -jar bin/fairness2excel.jar -r agroportal -o all
    ```
  * ```
    java -jar bin/fairness2excel.jar -r stageportal -o FCU,GEMET2 -f AIRnessResults-FCU-GEMET2.xlsx
     ```
